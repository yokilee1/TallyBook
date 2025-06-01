### AI对话记账本APP项目说明与产品原型（Android with Kotlin）

#### 项目目标

这款应用旨在为用户提供一个便捷的财务管理工具，通过AI对话简化记账流程，并通过可视化图表帮助用户分析消费习惯。目标用户为需要管理日常开支的个人，特别是偏好聊天式交互的用户。

#### 功能概述

- **AI对话**：用户可通过聊天界面查询支出、获取建议或自动分类交易，例如“本月餐饮花了多少？”。
- **记账功能**：支持手动输入交易或通过扫描收据自动录入，记录包括日期、金额、类别和备注。
- **数据可视化**：生成消费饼图（按类别分布）和折线图（按时间趋势），支持筛选时间范围。
- **用户界面**：主界面参考微信，包含顶部导航、聊天区和底部输入区域，快捷图标提供快速功能访问。

#### 开发建议

使用Kotlin和Android Studio开发，采用MVVM架构和Jetpack Compose（或XML布局）实现UI，结合Room数据库和MPAndroidChart库实现核心功能。集成Google Dialogflow和ML Kit支持AI交互和OCR功能。

------

### 详细项目说明与产品原型

#### 项目概述

- **项目名称**：AI记账本（AI Accounting Book）
- **目标**：开发一款Android原生应用，结合AI技术和微信风格的聊天界面，提供智能财务管理体验。
- **目标用户**：需要管理日常开支的个人用户，特别是希望通过对话简化记账流程的用户。
- **核心价值**：通过AI交互和直观UI，提供便捷的记账和财务分析功能。

#### 功能需求

##### AI交互功能

- 功能描述：
  - 用户通过聊天界面与AI进行自然语言交互，支持以下场景：
    - 查询财务数据（如“我上周花了多少钱？”）。
    - 获取消费建议（如“如何减少娱乐支出？”）。
    - 自动分类交易（如“这笔100元的消费是餐饮还是交通？”）。
  - AI需支持中文输入，理解财务相关术语，并保持对话上下文连贯性。
- 实现方式：
  - 集成[Google Dialogflow](https://cloud.google.com/dialogflow)处理自然语言查询，使用其Android SDK。
  - 可选：使用[IBM Watson](https://www.ibm.com/watson)作为替代AI服务。
  - 配置AI代理以支持财务领域特定意图（如查询、分类、建议）。

##### 基本记账功能

- 功能描述：
  - 交易录入：
    - 手动输入：用户输入日期（默认当前日期）、金额、类别（餐饮、交通、娱乐等）和备注。
    - 自动录入：通过摄像头扫描收据，使用OCR提取金额、日期等信息。
  - 历史记录：
    - 显示所有交易记录，支持按日期、类别或金额筛选。
    - 提供搜索功能，允许用户查找特定交易。
  - 数据可视化：
    - 消费流水饼图：展示支出按类别分布（如餐饮30%、交通20%）。
    - 开销折线图：展示日、周、月支出趋势。
- 实现方式：
  - 使用[Room](https://developer.android.com/training/data-storage/room)数据库存储交易数据。
  - 使用[MPAndroidChart](https://github.com/PhilJay/MPAndroidChart)生成饼图和折线图。
  - 集成[Google ML Kit](https://developers.google.com/ml-kit/text-recognition)实现收据OCR扫描。

##### 用户界面设计

- **设计灵感**：参考微信聊天界面和附件图像，确保简洁直观。
- 主界面布局：
  - 顶部：
    - 左侧：返回按钮（←）。
    - 中间：标题“AI记账本”（黑体中文）。
    - 右侧：菜单按钮（三点图标，访问设置、帮助等）。
  - 中间：
    - 聊天区：显示AI交互记录或交易摘要（如“今日支出：￥100”）。
  - 底部：
    - 输入框：支持文本输入，右侧有蓝色“发送”按钮。
    - 快捷功能图标：摄像头、麦克风、附件等，排列在输入框下方。
- 其他页面：
  - 交易录入页面：
    - 字段：日期、金额、类别（下拉菜单）、备注（可选）。
    - 附件上传：支持上传收据图片。
  - 报告页面：
    - 显示饼图和折线图，支持日期范围筛选（最近7天、30天等）。
  - 设置页面：
    - 用户信息（昵称、头像）。
    - 数据同步（云端备份，推荐使用[Firebase](https://firebase.google.com/)）。
    - 语言选择（默认中文，可扩展英文）。
    - 安全设置（密码保护、指纹解锁）。
- **颜色方案**：蓝色和白色为主，背景为浅灰色，遵循[Material Design](https://m3.material.io/)原则。

##### 多模态输入

- 功能描述：
  - **文本输入**：通过EditText输入交易信息或查询。
  - **语音输入**：通过麦克风录入交易或查询，转换为文本。
  - **图片输入**：扫描收据，OCR提取信息。
- 实现方式：
  - 语音输入：使用Android的[SpeechRecognizer](https://developer.android.com/reference/android/speech/SpeechRecognizer)。
  - 图片输入：使用Google ML Kit的文本识别功能。

##### 快捷功能图标

- **功能描述**：提供快速访问常用功能，参考附件中的图标网格。

- 图标列表：

  

  | 图标   | 功能     | 描述                 |
  | ------ | -------- | -------------------- |
  | 摄像头 | 扫描收据 | 使用OCR提取收据信息  |
  | 加号   | 添加交易 | 跳转到交易录入页面   |
  | 饼图   | 查看报告 | 显示消费饼图和折线图 |
  | 齿轮   | 设置     | 访问用户设置         |
  | 麦克风 | 语音输入 | 录入语音指令         |
  | 帮助   | 帮助中心 | 查看使用指南         |

#### 技术架构

##### 前端

- **语言**：Kotlin
- **IDE**：[Android Studio](https://developer.android.com/studio)
- **UI框架**：[Jetpack Compose](https://developer.android.com/jetpack/compose)（推荐，现代化UI开发）或传统XML布局（使用ConstraintLayout）。
- **图表库**：[MPAndroidChart](https://github.com/PhilJay/MPAndroidChart)
- **数据库**：[Room](https://developer.android.com/training/data-storage/room)
- **架构**：采用MVVM（Model-View-ViewModel）模式，确保代码结构清晰和可测试性。
- **异步编程**：使用[Kotlin Coroutines](https://kotlinlang.org/docs/coroutines-overview.html)处理数据库操作、网络请求等异步任务。

##### 后端

- **服务器**：使用[Firebase](https://firebase.google.com/)进行数据同步和云存储，或搭建自定义后端（如Node.js）。
- **数据安全**：使用Android Keystore加密敏感数据，确保财务信息安全。

##### AI与多模态支持

- **AI接口**：[Google Dialogflow](https://cloud.google.com/dialogflow)或[IBM Watson](https://www.ibm.com/watson)
- **OCR**：[Google ML Kit](https://developers.google.com/ml-kit/text-recognition)
- **语音识别**：Android [SpeechRecognizer](https://developer.android.com/reference/android/speech/SpeechRecognizer)

#### 开发计划

##### 阶段一：原型开发

- 设计UI布局，使用Jetpack Compose或XML。
- 实现基本聊天界面（使用模拟AI响应）。
- 搭建Room数据库，支持交易录入和查询。

##### 阶段二：AI集成

- 配置Dialogflow代理，集成Android SDK。
- 训练AI模型支持财务相关查询。

##### 阶段三：功能实现

- 实现手动和OCR交易录入。
- 开发数据可视化模块（饼图、折线图）。
- 支持多模态输入（文本、语音、图片）。

##### 阶段四：测试与优化

- 功能测试，确保数据准确性。
- 用户体验测试，优化UI/UX。
- 修复bug，确保应用稳定性。

#### 附加开发注意事项

- **响应性**：优化UI以适配不同屏幕尺寸和方向，使用ConstraintLayout或Compose的响应式布局。
- **本地化**：将字符串外部化，支持未来多语言扩展。
- **性能**：使用懒加载优化列表和图表渲染。
- **安全性**：加密财务数据，支持生物识别认证。

#### 参考附件分析

您提供的附件图像展示了一个名为“AI记账本”的应用界面，包含：

- **标题**：“AI记账本”，位于顶部中央。
- 布局：
  - 顶部：返回按钮（左侧）、菜单按钮（右侧）。
  - 中间：空白区域，可能用于聊天或摘要显示。
  - 底部：输入框（“请输入内容”）、发送按钮（“搜索”）、快捷功能图标（摄像头、加号、齿轮等）。
- **颜色方案**：蓝色和白色为主，背景为浅灰色。 这些元素已融入设计，确保与您的视觉参考一致。

#### 总结

本文档提供了使用Kotlin开发Android原生AI记账本APP的详细项目说明和产品原型描述。主界面模仿微信聊天UI，支持AI交互、交易录入、数据可视化和多模态输入。

 [原型文件.pdf](原型文件.pdf) 
