 //品牌控制层 
app.controller('baseController' ,function($scope){	
	
    //重新加载列表 数据
    $scope.reloadList=function(){
    	//切换页码  
    	$scope.search( $scope.paginationConf.currentPage, $scope.paginationConf.itemsPerPage);	   	
    }
    
	//分页控件配置 
	$scope.paginationConf = {
         currentPage: 1,
         totalItems: 10,
         itemsPerPage: 10,
         perPageOptions: [10, 20, 30, 40, 50],
         onChange: function(){
        	 $scope.reloadList();//重新加载
     	 }
	}; 
	
	$scope.selectIds=[];//选中的ID集合 

	//更新复选
	$scope.updateSelection = function($event, id) {		
		if($event.target.checked){//如果是被选中,则增加到数组
			$scope.selectIds.push( id);			
		}else{
			var idx = $scope.selectIds.indexOf(id);
            $scope.selectIds.splice(idx, 1);//删除 
		}
	}
	//下面的方法可以将传入的json串对象转换为json中的某一项输出
	$scope.jsonToString=function (jsonString,key) {
		//1.将传入的json串转换为json对象
		var jsonObj = JSON.parse(jsonString);
		//2.遍历json对象，拼接成字符串的形式显示
		var info = ""; //最终拼接后的字符串
		for (var i = 0,len = jsonObj.length;i < len;i++){
			if (i > 0){
				info += ",";
			}
			info += jsonObj[i][key];
		}
		//3.返回拼接后的字符串
		return info;
	}
});	