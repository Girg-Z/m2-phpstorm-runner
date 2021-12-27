package com.github.girgz.m2phpstormrunner.util.magento

import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.openapi.vfs.VfsUtil

class MagentoBasePathUtil {

    companion object {
        fun isMagentoFolderValid(path: String?): Boolean {
            if (path.isNullOrBlank()) {
                return false;
            }

            val file = LocalFileSystem.getInstance().findFileByPath(path)
            return if (file != null && file.isDirectory) {
                // https://kotlinlang.org/docs/functions.html#infix-notation
                // * Spread operator
                VfsUtil.findRelativeFile(file, *"vendor/magento/framework".split("/").toTypedArray()) != null
            } else false
        }
    }
}