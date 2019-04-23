var brandController = app.controller("brandController",function ($scope,brandService,$http) {
    //这里定义关于分页的配置：
    $scope.paginationConf = {
        currentPage : 1,					//当前页
        totalItems : 10,					//总记录数
        itemsPerPage : 5,					//每页的记录数
        perPageOptions: [10, 20, 30, 40, 50],  //分页选项
        onChange : function(){				//此函数触发时机：①窗体加载完毕 ②点击上下页按钮时
            //1.查询所有品牌带分页
            //$scope.findByPage($scope.paginationConf.currentPage,$scope.paginationConf.itemsPerPage);
            //2.条件查询带有分页功能
            $scope.search();
        }
    }
    //这里定义要删除的品牌id列表
    $scope.brandIds = [];
    //2.3)下面都是根据业务需求来定义自己的方法
    //① 查询所有的品牌(不带分页)
    $scope.findAll= ()=>{
        brandService.findAll().success(response=>{
            $scope.list = response;
        })
    }
    //② 查询所有的商品（带有分页功能）
    $scope.findByPage= (page,pagesize)=>{
       brandService.findByPage(page, pagesize).success(response=>{
            //查询出数据后，要做两件事情：
            //第一件事情：取得分页的数据
            $scope.list = response.rows;
            //第二件事情：对分页选项中的所有的记录数赋值
            $scope.paginationConf.totalItems = response.total;
        })
    }
    //③ 查询商品（条件查询+分页功能）
    $scope.search = ()=>{
        var page = $scope.paginationConf.currentPage;       //代表当前页
        var pagesize = $scope.paginationConf.itemsPerPage;  //代表每页大小
        brandService.search(page, pagesize,$scope.searchEntity).success(response=>{
            //查询出数据后，要做两件事情：
            //第一件事情：取得分页的数据
            $scope.list = response.rows;
            //第二件事情：对分页选项中的所有的记录数赋值
            $scope.paginationConf.totalItems = response.total;
        })
    }
    //④ 保存商品（添加/修改）
    $scope.save =()=>{
        //1.区分是添加还是修改，使用$scope.entity.id是否有值
        var url = "../brand/add.do";
        if ($scope.entity.id){
            url = "../brand/update.do"
        }
        //2.根据上面的url地址来执行不同的操作
       brandService.save(url, $scope.entity).success(response =>{
            if (response.success){      //保存成功
                $scope.search();        //刷新列表
            } else{
                alert(response.message);
            }
        } )
    }
    //⑤ 显示修改页面中的数据
    $scope.update = (brand)=>{
        $scope.entity = brand;
    }
    //⑥ 当我们选择要删除的某个商品时
    $scope.userSelect = (event,brandId)=>{
        if (event.target.checked){  //如果是复选状态，就将当前id值设置给$scope.brandIds变量，event.target:代表我们点击的哪个复选框控件
            $scope.brandIds.push(brandId);
            //也可写出如下形式:
            //$scope.brandIds[$scope.brandIds.length] = brandId;
        } else{                    //如果没有被复选就从数组中删除它
            //1.根据当前要删除的品牌的id找到其在数组中的下标
            var index = $scope.brandIds.indexOf(brandId);
            //2.从数组中删除指定的id
            $scope.brandIds.splice(index,1);   //参数1:代表要删除的元素在数组中的索引 参数2：代表要删除的元素个数
        }
    }
    //⑦ 删除指定元素
    $scope.delete = ()=>{
        brandService.delete($scope.brandIds).success(response=>{
            if (response.success){      //删除成功
                $scope.search();        //刷新列表
            } else{
                alert(response.message);
            }
        })
    }


})