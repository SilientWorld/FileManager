
---

# 文件管理器实现

## 项目简介

该项目实现了一个完整的文件管理器应用，能够对文件和文件夹进行多种管理操作，并支持丰富的功能，如文件的复制、剪切、移动、删除、新建、搜索等。除此之外，项目还包括了文件的打开、排序、后缀名显示与隐藏等功能，为用户提供了一个方便且功能强大的文件管理工具。

## 功能要求

1. **基本文件管理功能**：包括对文件或文件夹进行复制、剪切、移动、删除、新建、搜索等操作。
2. **支持多文件或多文件夹操作**：能够支持同时对多个文件或文件夹执行操作，如同时删除多个文件、同时操作一个目录下的文件和文件夹等。
3. **文件关联功能**：能够根据文件的类型（如 HTML 文件、MP3 文件等）调用系统默认应用打开文件，若找不到对应模块，显示“无法打开”提示。
4. **后缀名显示/隐藏功能**：用户可以选择是否显示文件的后缀名。
5. **文件列表展示**：以列表的形式展示文件和目录，且使用图片区分不同类型的文件，采用类似 Windows 操作系统的文件管理方式。
6. **排序功能**：支持按文件时间或大小进行排序，方便用户快速定位文件。
7. **拖拽操作**（可选）：支持将文件拖拽到文件夹中，或将多个文件拖拽到一起自动合并为一个新文件夹。
8. **Android 演示**：该项目将在 Android 系统上实现，并提供完整的手机模拟器演示。

## 技术要求

- **编程环境**：使用 **Android Studio** 开发 Android 应用。
- **安卓模拟器**：建议使用手机模拟器进行测试和演示。
- **文件管理实现**：通过 Android 的文件操作 API 实现文件和目录的管理功能。

## 项目结构

- **主要功能类**：包括文件管理、文件操作、文件排序、文件类型处理等。
- **布局设计文件**：项目中包含多种布局文件，用于展示文件列表、文件信息、排序选项等界面元素。
- **资源文件**：包括文件图标、排序选项等资源，方便用户区分文件类型和操作方式。

## 核心功能实现

- **文件管理操作**：实现了文件的复制、剪切、移动、删除、新建、搜索等操作。
- **文件类型处理**：支持根据文件类型调用不同的应用打开文件。例如，HTML 文件通过系统浏览器打开，MP3 文件通过音乐播放器打开。
- **后缀名显示与隐藏**：用户可以选择是否显示文件的后缀名，增强灵活性。
- **文件排序**：支持按文件的创建时间或大小进行排序，以便用户快速查找文件。
- **文件拖拽**（可选）：用户可以通过拖拽操作管理文件，支持将文件拖拽到文件夹中或合并文件为新文件夹。

## 如何运行

1. **打开项目**：将项目导入到 Android Studio 中。
2. **同步 Gradle**：确保已经同步了所有的 Gradle 配置文件，特别是文件操作相关的库。
3. **选择模拟器**：选择手机模拟器进行测试。
4. **运行应用**：点击运行按钮，启动应用并开始演示文件管理功能。

## 参与开发

如果您对该项目有兴趣或希望为该项目贡献代码，可以按照以下步骤进行开发：

1. Fork 本项目到您的 GitHub 账户。
2. 克隆项目到本地进行开发。
3. 提交 Pull Request，说明您的修改或新增功能。
