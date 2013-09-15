//f6cc6120-205c-4e5b-bdad-0f26156c85be
var config = {headers:  {
        'Authorization': 'Bearer f6cc6120-205c-4e5b-bdad-0f26156c85be',
        'Accept': 'application/json'
    }
};
function MeCtrl($scope, $http) {
  $http.get('/application/users',config).success(function(data){
    $scope.user = data;
  });
}