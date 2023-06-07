Building Oboe for Android (on Windows, Android Studio installed)
======================================
- Clone the repository: `git clone https://github.com/google/oboe.git` 
- Navigate to the repository
- Checkout the desired tag/branch
- Create a build directory for each architecture (arm64-v8a, armeabi-v7a, x86, x86_64)
- Run the following commands:
    - `set NDK=%APPDATA%/../Local/Android/Sdk/ndk-bundle`
    - `set NINJA=%APPDATA%/../Local/Android/Sdk/cmake/3.6.4111459/bin/ninja.exe`
    - `set TOOLCHAIN=%APPDATA%/../Local/Android/Sdk/ndk-bundle/build/cmake/android.toolchain.cmake`
- For each architecture:
    - Navigate to the archiecture directory
    - Run the following CMake command: `cmake -DANDROID_DEPRECATED_HEADERS=ON -DANDROID_STL=c++_static -DANDROID_ABI=<ARCH> -DANDROID_PLATFORM=<PLATFORM> -DANDROID_NDK=%NDK% -DCMAKE_TOOLCHAIN_FILE=%TOOLCHAIN% -DCMAKE_MAKE_PROGRAM=%NINJA% -G"Ninja" ..`
        - For <ARCH> `arm64-v8a` and `x86_64`, <PLATFORM> should be `android-21`
        - For <ARCH> `armeabi-v7a` and `x86`, <PLATFORM> should be `android-19`
    - Run `%NINJA%`
    - The compiled library for the target architecture will be at your current directory as `liboboe.a`