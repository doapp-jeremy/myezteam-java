var token = localStorage.getItem('token');
var config = {headers:  {
        'Authorization': 'Bearer ' + token,
        'Accept': 'application/json'
    }
};
/*
angular.module('teamServices', '[ngResource]').
  factory('Team', function($resource){
  var Team = $resource('/application/teams/:uuid', {uuid: '@uuid'}, {update: { method: 'PUT' }});
  angular.extend(Team.prototype, {
    save: function(values) {
      if (values) {
        angular.extend(this,values);
      }
      if (this.uuid) {
        return this.$update();
      }
      return this.$save();function MeCtrl($scope, $http) {
        $http.get('/application/users',config).success(function(data){
          $scope.user = data;
        });
      }

      function TeamCtrl($scope, $http) {
        $http.get('/application/teams',config).success(function(data){
          $scope.teams = data;
        }).error(function(error){
          window.location = "/application/auth/login";
        });
      }

    }
  });
});
angular.module('userServices', '[ngResource]').
  factory('User',function($resource){
    return $resource('/application/users', {}, {
      me: {method: 'GET', isArray: false }
    });
});
*/
