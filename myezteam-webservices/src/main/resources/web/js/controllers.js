var token = localStorage.getItem('token');
var config = {headers:  {
        'Authorization': 'Bearer ' + token,
        'Accept': 'application/json'
    }
};

angular.module('userServices', ['ngResource'])
  .factory('User', ['$resource', function($resource) {
  var User = $resource('/application/users', {}, {
      me: { method: 'GET' }
  });

  return User;
}]);

function MeCtrl($scope, User) {
  $scope.user = User.me();
}

function TeamCtrl($scope, $http) {
  $http.get('/application/teams',config).success(function(data){
    $scope.teams = data;
  }).error(function(error){
    window.location = "/application/auth/login";
  });
}
