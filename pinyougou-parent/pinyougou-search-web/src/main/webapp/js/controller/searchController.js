app.controller("searchController",function ($scope,$controller,searchService) {
    $controller("baseController",{$scope:$scope});
    $scope.search =function () {
        searchService.search($scope.searchEntity).success(function (response) {
            $scope.resultMap = response;
        })
    }
})
