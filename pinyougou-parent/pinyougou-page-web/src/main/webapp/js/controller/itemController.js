app.controller("itemController",function($http,$scope){
	$scope.addNum=function(x){
		$scope.num = $scope.num + x;
		if($scope.num < 1){
			$scope.num = 1;
		}
	}
	//添加到购物车
	$scope.addToCart=function(){
		//参数{'withCredentials':true}代表客户端同意服务端发送cookie及http认证信息
		$http.get("http://localhost:9107/cart/addGoodsToCartList.do?itemId="+$scope.sku.id+"&num="+$scope.num,{'withCredentials':true});
	}
	//定义存放当前用户选择的规格
	$scope.specificationItems={};
	//用户点击某个规格时执行
	$scope.selectItem=function(key,value){
		$scope.specificationItems[key] = value;
		selectObject();
	}
	//用于判断当前点击的规格是否与临时存放的规格数据(即：$scope.specificationItems)一致
	$scope.isSelected=function(key,value){
		/*
		if($scope.specificationItems[key] == value){
			return true;
		}
		return false;
		*/
		return $scope.specificationItems[key] == value;
	}
	//加载sku商品
	$scope.loadSku=function(){
		$scope.sku=skuList[0];
		//对$scope.specificationItems赋初始值
		$scope.specificationItems=JSON.parse(JSON.stringify($scope.sku.spec));
	}
	//比较两个对象是否相等
	matchObject=function(map1,map2){
		for(var k in map1){
			if(map1[k] != map2[k]){
				return false;
			}
		}
		for(var k in map2){
			if(map2[k] != map1[k]){
				return false;
				
			}
		}
		return true;
	}
	//当选择某个规格选项时，就判断skuList列表中是否有此spec规格，如果有，就对$scope.sku
	//变量赋值，从而在页面显示此sku商品信息
	selectObject=function(){
		//1.遍历skuList列表
		for(var i=0;i< skuList.length;i++){
			var b = matchObject(skuList[i].spec,$scope.specificationItems);
			if(b){
				$scope.sku=skuList[i];
				return ;
			}
			
		}
		$scope.sku={id:0,title:'--------',price:0};//如果没有匹配的
	}
})