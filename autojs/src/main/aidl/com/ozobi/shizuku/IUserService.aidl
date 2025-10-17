// IUserService.aidl
package com.ozobi.shizuku;

// Declare any non-default types here with import statements

interface IUserService {
        void destroy() = 16777114; // Destroy method defined by Shizuku server

        void exit() = 1; // Exit method defined by user
        /**
         * 执行命令
         */
        String execCommand(String command) = 2;
        /**
         * 执行数组中分离的命令
         */
        String execArr(in String[] command) = 3;

        String currentPackage() = 11;
        String currentActivity() = 12;
        String currentComponent() = 13;
        String currentComponentShort() = 14;
}