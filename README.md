# 俄罗斯粑粑块 - 游戏项目说明

## 项目概述
这是一个使用Gradle 7.3.1构建的Android游戏项目，基于S-Venti制作的鬼畜视频《俄罗斯粑粑块》改编。
原视频链接https://www.bilibili.com/video/BV1maVTzDEYr/?spm_id_from=333.1007.top_right_bar_window_history.content.click

## 音乐文件配置

### 问题说明
当前项目中，音乐文件路径配置为外部绝对路径：`G:\FFOutput\game-music.mp3`，这是因为：
1. 我无法直接访问或复制您本地文件系统中的音乐文件
2. 这是您在需求中指定的文件路径

### 解决方案
建议将音乐文件添加到项目资源中，这样更可靠且便于版本控制：

1. **将音乐文件添加到项目资源**：
   - 在`app/src/main/res`目录下创建`raw`文件夹（如果不存在）
   - 将`game-music.mp3`文件复制到`app/src/main/res/raw/`目录

2. **修改MusicService.java**：
   ```java
   // 将当前的MediaPlayer初始化代码
   mediaPlayer = new MediaPlayer();
   mediaPlayer.setDataSource("G:\\FFOutput\\game-music.mp3");
   mediaPlayer.prepare();
   
   // 替换为：
   mediaPlayer = MediaPlayer.create(this, R.raw.game_music);
   ```

3. **修改AndroidManifest.xml**：
   - 移除不需要的外部存储权限
   ```xml
   <!-- 移除这行 -->
   <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
   ```

## 构建说明

1. 确保已配置Android SDK路径（在local.properties中）
2. 运行以下命令构建项目：
   ```
   E:\gradle-7.3.1\bin\gradle.bat assembleDebug
   ```
3. 构建成功后，APK文件将生成在`app/build/outputs/apk/debug/`目录

## 项目结构

- `SplashActivity`：启动加载界面
- `MainMenuActivity`：主菜单界面
- `AboutActivity`：关于页面
- `MusicService`：音乐播放服务

## 功能特点

- 启动加载动画
- 主菜单功能按钮
- 关于页面展示
- 音乐循环播放与淡出效果
