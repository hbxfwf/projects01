 //控制层 
app.controller('goodsController' ,function($scope,$controller,$location,typeTemplateService,itemCatService,goodsService,uploadService){
	
	$controller('baseController',{$scope:$scope});//继承
	//定义文件上传方法
	$scope.uploadFile=function () {
		uploadService.uploadFile().success(
			function (response) {
				if (response.success){

					$scope.image_entity.url = response.message;
				} else{
					alert(response.message);
				}
			}
		)
	}
	//初始化entity对象
	$scope.entity={goods:{},goodsDesc:{itemImages:[],customAttributeItems:[],specificationItems:[]}};
	//将刚上传的文件保存到文件列表中
	$scope.addImagesList=function(){

		$scope.entity.goodsDesc.itemImages.push($scope.image_entity);
	}
	//从图像列表中删除图片
	$scope.delImage=function(index){
		$scope.entity.goodsDesc.itemImages.splice(index, 1);
	}
    //读取列表数据绑定到表单中  
	$scope.findAll=function(){
		goodsService.findAll().success(
			function(response){
				$scope.list=response;
			}			
		);
	}
	//分页
	$scope.findPage=function(page,rows){			
		goodsService.findPage(page,rows).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
	//查询实体 
	$scope.findOne=function(){
		//$location.search():是angularjs从上个页面取值的固定语法，而["id"]，代表传值时使用的参数
		var id = $location.search()["id"];
		if (!id ){
			return ;
		}
		goodsService.findOne(id).success(
			function(response){

				$scope.entity= response;
				//对富文本编辑器赋值
				editor.html($scope.entity.goodsDesc.introduction);
				//处理图片
				$scope.entity.goodsDesc.itemImages=JSON.parse($scope.entity.goodsDesc.itemImages);
				//处理扩展属性
				$scope.entity.goodsDesc.customAttributeItems=JSON.parse($scope.entity.goodsDesc.customAttributeItems);
				//处理规格及规格选项
				$scope.entity.goodsDesc.specificationItems=JSON.parse($scope.entity.goodsDesc.specificationItems);
				//处理sku列表中的spec这个json串
				var items = $scope.entity.items;
				for (var i=0;i < items.length;i++){
					items[i].spec = JSON.parse(items[i].spec);
				}
			}
		);				
	}
	//根据当前的商品id查询出商品信息，让其规格自动选择
	$scope.checkAttributeValue=function(specName,optionName){
		//1.得到规格选项列表
		var specItems = $scope.entity.goodsDesc.specificationItems;
		//根据规格名称，查询是否存在此规格对象
		let object = $scope.searchObjectByKey(specItems,"attributeName",specName);
		if (object){
			return object.attributeValue.indexOf(optionName) >= 0;
		}
	}
	//保存 
	$scope.save=function(){				
		var serviceObject;//服务层对象
		//将富文本编辑器的内容写入到goodsDesc的introduction 这个字段中
		//editor.html();得到富文本编辑器的输入内容
		$scope.entity.goodsDesc.introduction=editor.html();
		if($scope.entity.goods.id!=null){//如果有ID
			serviceObject=goodsService.update( $scope.entity ); //修改  
		}else{
			serviceObject=goodsService.add( $scope.entity  );//增加 
		}				
		serviceObject.success(
			function(response){
				if(response.success){
					//重新查询 
		        	$scope.reloadList();//重新加载
					//清空富文本编辑器的内容
					editor.html("");
					$scope.entity = {};
				}else{
					alert(response.message);
				}
			}		
		);				
	}
	//批量删除 
	$scope.dele=function(){			
		//获取选中的复选框			
		goodsService.dele( $scope.selectIds ).success(
			function(response){
				if(response.success){
					$scope.reloadList();//刷新列表
					$scope.selectIds=[];
				}						
			}		
		);				
	}
	
	$scope.searchEntity={};//定义搜索对象 
	$scope.status=["未审核","己审核","审核未通过","己关闭"];

	//搜索
	$scope.search=function(page,rows){			
		goodsService.search(page,rows,$scope.searchEntity).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
	//定义所有的分类集合
	$scope.itemCats = [];
	//查询所有的分类
	$scope.findItemCatList=function(){
		itemCatService.findAll().success(
			function (response) {
				for (var i = 0; i <response.length;i++){
					//以商品分类为key存到数组$scope.itemCats中，到时显示时就从这个数组中取值
					$scope.itemCats[response[i].id] = response[i].name;
				}
			}
		)
	}
    //根据父id查询其子节点列表
	$scope.findByParentId=function (parentId) {
		itemCatService.findByParentId(parentId).success(
			function (response) {
				$scope.itemCatList = response;
			}
		)
	}
	//监控一级分类选项发生改变(自动填充二级分类)
	$scope.$watch("entity.goods.category1Id",function (newValue,oldValue) {
		itemCatService.findByParentId(newValue).success(
			function (response) {
				$scope.itemCat2List = response;
			}
		)
	})
	//监控二级分类选项发生改变(自动填充三级分类)
	$scope.$watch("entity.goods.category2Id",function (newValue,oldValue) {
		itemCatService.findByParentId(newValue).success(
			function (response) {
				$scope.itemCat3List = response;
			}
		)
	})
	//监控三级分类选项发生改变(自动填充商品的模板id)
	$scope.$watch("entity.goods.category3Id",function (newValue,oldValue) {
		//1.根据商品分类id在tb_itemcat表中查询出此分类关联的模板id
		itemCatService.findOne(newValue).success(function (response) {
			//1.1)取得模板对象的id
			$scope.entity.goods.typeTemplateId=response.typeId;
		})
	})
	/**
	 * @Author: Feng.Wang
	 * @Date: 2019/4/27 10:37
	 * @Company: Zelin.ShenZhen
	 * @ClassName:
	 * @Description: 动态生成集合：$scope.entity.goodsDesc.specificationItems
	 */
	$scope.updateSpecAttribute=function (event,name,value) {
		//1.在指定的集合中查询是否存在指定key和value的元素
		let object = $scope.searchObjectByKey($scope.entity.goodsDesc.specificationItems,"attributeName",name);
		//2.如果存在此对象，再根据是否复选来决定向attributeValue添加或删除指定的内容value
		if (object != null){
			if (event.target.checked){	//2.1)如果被复选，就向attributeValue中添加选中项value的值
				object.attributeValue.push(value);
			}else{						//2.2)如果未被复选，就从attributeValue中删除指定的value的值
				object.attributeValue.splice(object.attributeValue.indexOf(value),1);
				//2.3)如果当前的attribute没有值，则删除此对象
				if (object.attributeValue.length == 0){
					$scope.entity.goodsDesc.specificationItems.splice($scope.entity.goodsDesc.specificationItems.indexOf(object),1);
				}
			}
		} else{
			$scope.entity.goodsDesc.specificationItems.push({"attributeName":name,"attributeValue":[value]});
		}
	}
	//生成sku列表（即设置tbItem表的数据，添加一条spu，有可能向sku中添加多条数据）
	$scope.createItemList=function(){
		//1.初始化$scope.entity.items
		$scope.entity.items=[{spec:{},price:0,num:99999,status:'0',isDefault:'0' }];
		//2.取得$scope.entity.goodsDesc.specificationItems的数据
		var specificationItems = $scope.entity.goodsDesc.specificationItems;
		//3.遍历其中的数据，并向$scope.entity.items中添加数据
		for (var i=0,len=specificationItems.length;i<len;i++){
			$scope.entity.items=addColumn($scope.entity.items,
										specificationItems[i].attributeName,
										specificationItems[i].attributeValue);
		}
	}
	//动态数据到$scope.entity.items中
	function addColumn(list,attributeName,attributeValue){
		//1.定义一个新的集合
		var newList = [];
		//2.遍历集合，取得原始行，再利用对象的深克隆技术，得到新行，并向新行添加数据
		for (var i=0;i<list.length;i++){
			//2.1)得到旧行
			var oldRow = list[i];
			for (var j=0;j<attributeValue.length;j++){
				//2.2)根据旧行克隆出新行
				var newRow = JSON.parse(JSON.stringify(oldRow));  //对象的深克隆，原来的对象与新对象完全脱离关系
				//2.3)向新行中添加数据
				newRow.spec[attributeName] = attributeValue[j];
				//2.4)将新行添加到newList集合中
				newList.push(newRow);
			}

		}

		return newList;
	}
	//监控模板id，此时得到模板关联的品牌列表
	$scope.$watch("entity.goods.typeTemplateId",function(newValue,oldValue) {
		typeTemplateService.findOne(newValue).success(
			function (response) {
				//第一件事情：显示此模板关联的品牌列表
				//1.1)取得模板对象
				$scope.typeTemplate = response;
				//1.2)得到模板对象的关联品牌列表对象(将json串转换为对象)
				$scope.typeTemplate.brandIds=JSON.parse($scope.typeTemplate.brandIds);
				//第二件事情：就是显示关联的扩展属性
				//$scope.typeTemplate.customAttributeItems=JSON.parse($scope.typeTemplate.customAttributeItems);
				//判断是否是通过“修改”按钮过来的
				var id = $location.search()["id"];
				if (!id) {
					$scope.entity.goodsDesc.customAttributeItems=JSON.parse($scope.typeTemplate.customAttributeItems);
				}


			}
		)
		//第三件事情：就是根据模板id查询规格及规格选项列表
		typeTemplateService.findSpecList(newValue).success(
			function (response) {
				$scope.specList = response;
			}
		)
	})

});	
