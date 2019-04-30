app.service("searchService",function ($http) {
    //根据查询关键字进行搜索
    this.search =function (entity) {
        return $http.post("./itemSearch/search.do",entity);
    }
})