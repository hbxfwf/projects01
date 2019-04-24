 //控制层 
app.controller('typeTemplateController' ,function($scope,$controller,specificationService,brandService,typeTemplateService){
	
	$controller('baseController',{$scope:$scope});//继承
	
    //读取列表数据绑定到表单中  
	$scope.findAll=function(){
		typeTemplateService.findAll().success(
			function(response){
				$scope.list=response;
			}			
		);
	}    
	
	//分页
	$scope.findPage=function(page,rows){			
		typeTemplateService.findPage(page,rows).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
	
	//查询实体 
	$scope.findOne=function(id){				
		typeTemplateService.findOne(id).success(
			function(response){
				$scope.entity= response;					
			}
		);				
	}
	
	//保存 
	$scope.save=function(){				
		var serviceObject;//服务层对象  				
		if($scope.entity.id!=null){//如果有ID
			serviceObject=typeTemplateService.update( $scope.entity ); //修改  
		}else{
			serviceObject=typeTemplateService.add( $scope.entity  );//增加 
		}				
		serviceObject.success(
			function(response){
				if(response.success){
					//重新查询 
		        	$scope.reloadList();//重新加载
				}else{
					alert(response.message);
				}
			}		
		);				
	}
	
	 
	//批量删除 
	$scope.dele=function(){			
		//获取选中的复选框			
		typeTemplateService.dele( $scope.selectIds ).success(
			function(response){
				if(response.success){
					$scope.reloadList();//刷新列表
					$scope.selectIds=[];
				}						
			}		
		);				
	}
	
	$scope.searchEntity={};//定义搜索对象 
	
	//搜索
	$scope.search=function(page,rows){			
		typeTemplateService.search(page,rows,$scope.searchEntity).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
	//取出品牌列表（返回List<Map>供select2使用）
	$scope.selectBrandList = function () {
		brandService.selectBrandList().success(function (response) {
			$scope.brandList = {data:response};
		})
	}
	//取出规格列表（返回List<Map>供select2使用）
	$scope.findSpecList = function () {
		specificationService.findSpecList().success(function (response) {
			$scope.specList = {data:response};
		})
	}
	//修改模板的界面
	$scope.updateTemp=function (template) {
		$scope.entity = template;
		//因为从数据库中取出的brandIds，specIds,customAttributeItems这些内容是json字符串，我们要正确显示，必须要将其转换
		//json对象
		$scope.entity.brandIds = JSON.parse(template.brandIds);
		$scope.entity.specIds = JSON.parse(template.specIds);
		$scope.entity.customAttributeItems = JSON.parse(template.customAttributeItems);
	}
});	
