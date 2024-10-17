use jni::objects::{JClass, JString};
use jni::sys::{jlong, jstring};
use jni::JNIEnv;
use std::{fs, i64};
use walkdir::WalkDir;

#[no_mangle]
pub extern "system" fn Java_com_dazuoye_filemanager_utils_FSHelper_getFolderSizeNative<'local>(
    mut env: JNIEnv<'local>,
    _: JClass<'local>,
    input: JString<'local>,
) -> jstring {
    let dir: String = env
        .get_string(&input)
        .expect("failed to parse input")
        .into();

    if !fs::exists(&dir).expect(format!("Cannot stat {}", dir).as_str()) {
        return env
            .new_string(format!("{} not exists!", dir))
            .expect("Couldn't create java string!")
            .into_raw();
    }
    // 从这里保证文件至少存在了

    let mut size: u64 = 0;
    for entry in WalkDir::new(dir) {
        match entry {
            Ok(item) => {
                match item.metadata() {
                    Ok(metadata) => size += metadata.len(),
                    Err(e) => {
                        eprintln!("Error getting metadata for item: {:?}", e);
                        continue;
                    }
                }
            },
            Err(e) => {
                eprintln!("Error walking directory: {:?}", e);
                continue;
            }
        }
    }
    

    let output = env
        .new_string(format_size(size))
        .expect("Couldn't create java string!");

    output.into_raw()
}

#[no_mangle]
pub extern "system" fn Java_com_dazuoye_filemanager_utils_FSHelper_getFolderSizeBytesNative<'local>(
    mut env: JNIEnv<'local>,
    _: JClass<'local>,
    input: JString<'local>,
) -> jlong {
    let dir: String = env
        .get_string(&input)
        .expect("failed to parse input")
        .into();

    if !fs::exists(&dir).expect(format!("Cannot stat {}", dir).as_str()) {
        return 0;
    }
    // 从这里保证文件至少存在了

    let mut size: u64 = 0;
    for entry in WalkDir::new(dir) {
        match entry {
            Ok(item) => {
                match item.metadata() {
                    Ok(metadata) => size += metadata.len(),
                    Err(e) => {
                        eprintln!("Error getting metadata for item: {:?}", e);
                        continue;
                    }
                }
            },
            Err(e) => {
                eprintln!("Error walking directory: {:?}", e);
                continue;
            }
        }
    }
    

    match i64::try_from(size) {
        Ok(compatible) => compatible,
        Err(_) => i64::MAX
    }
}

fn format_size(size: u64) -> String {
    // 定义单位
    let units = ["B", "KB", "MB", "GB", "TB", "PB"];
    let mut size = size as f64;
    let mut unit_index = 0;

    // 按 1024 递减计算，直到找到合适的单位
    while size >= 1024.0 && unit_index < units.len() - 1 {
        size /= 1024.0;
        unit_index += 1;
    }

    // 保留两位小数格式化输出
    format!("{:.2} {}", size, units[unit_index])
}