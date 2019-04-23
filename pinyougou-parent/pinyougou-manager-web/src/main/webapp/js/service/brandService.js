var brandService=app.service("brandService",function ($http) {
    //1.查询所有
    this.findAll = ()=>{
        return $http.get("../brand/list.do");
    }
    //2.分页查询
    this.findByPage = (page,pagesize)=>{
        return $http.get("../brand/findByPage.do?page="+page+"&pagesize="+pagesize);
    }
    //3.分页带条件查询
    this.search = (page,pagesize,entity)=>{
        return $http.post("../brand/search.do?page="+page+"&pagesize="+pagesize, entity);
    }
    //4.保存商品
    this.save = (url,entity)=>{
        return $http.post(url,entity);
    }
    //5.根据id删除品牌
    this.delete = (brandIds)=>{
        return $http.get("../brand/delete.do?ids="+brandIds);
    }
})