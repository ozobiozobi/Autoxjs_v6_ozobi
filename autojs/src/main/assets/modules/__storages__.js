
module.exports = function(__runtime__, scope){
    var storages = {};
    storages.create = function(name){
        return new LocalStorage(name);
    }

    storages.remove = function(name){
        this.create(name).clear();
    }

    // Added by ozobi - 2025/02/06 > 获取当前已有的本地存储名称
    storages.getExisting = function(isPath){
        let localStorageFilePathArr = [];
        let filesDir = context.getFilesDir().toString()
        let localStoragePath = filesDir.slice(0, filesDir.lastIndexOf("/")) + "/shared_prefs/"
        if (__runtime__.files.isDir(localStoragePath)) {
            let fileNameList = __runtime__.files.listDir(localStoragePath, (str) => { return str.startsWith("autojs.localstorage.") && str.endsWith(".xml") });
            if (fileNameList.length > 0) {
                for (let fileName of fileNameList) {
                    let fName;
                    if(isPath){
                        fName = localStoragePath + fileName;
                    }else{
                        fName = fileName.slice(20, fileName.lastIndexOf("."))
                    }
                    localStorageFilePathArr.push(fName);
                }
            }
        }
        return localStorageFilePathArr;
    }
    // <

    return storages;

    function LocalStorage(name){
        this._storage = new com.stardust.autojs.core.storage.LocalStorage(context, name);
        this.put = function(key, value){
            if(typeof(value) == 'undefined'){
                throw new TypeError('value cannot be undefined');
            }
            this._storage.put(key, JSON.stringify(value));
        }
        this.get = function(key, defaultValue){
            var value = this._storage.getString(key, null);
            if(!value){
                return defaultValue;
            }
            return JSON.parse(value);
        }
        this.remove = function(key){
            this._storage.remove(key);
        }
        this.contains = function(key){
            return this._storage.contains(key);
        }
        this.clear = function(key){
            this._storage.clear();
        }
    }
}

