 //控制层 
app.controller('indexController' ,function($scope,$controller   ,loginService){
	
	$controller('baseController',{$scope:$scope});//继承
	$scope.loginName=function () {
		loginService.loginName().success(function (response) {
			$scope.name=response.name;
		})
	}

});
