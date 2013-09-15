var token = localStorage.getItem('token');
var config = {headers:  {
        'Authorization': 'Bearer ' + token,
        'Accept': 'application/json'
    }
};
function MeCtrl($scope, $http) {
  $http.get('/application/users',config).success(function(data){
    $scope.user = data;
  });
}