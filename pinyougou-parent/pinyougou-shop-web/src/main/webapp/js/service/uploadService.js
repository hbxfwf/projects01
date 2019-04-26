app.service("uploadService",function ($http) {
    this.uploadFile=function () {
        //定义表单数据
        var formData = new FormData();
        formData.append("file",file.files[0]);
        return $http({
            url:'../upload.do',
            data:formData,
            method:'post',
            headers:{"Content-Type":undefined},  //让当前提交的表单头为：multipart/form-data,因为angularjs默认的请求头：application/json
            transformRequest:angular.identity   //让angularjs上传数据时，以序列化的方式上传
        })
    }
})