var token = localStorage.getItem("token");
angular.module('team', ['restangular']).
  config(function($routeProvider, RestangularProvider) {
    $routeProvider.
      when('/', {
        controller:ListCtrl, 
        templateUrl:'list.html'
      }).
      when('/:teamUUID', {
        controller:ViewCtrl,
        templateUrl:'view.html',
        resolve: {
          team: function(Restangular, $route) {
            return Restangular.one('teams', $route.current.params.teamUUID).get();
          }
        }
      }).
      when('/edit/:teamUUID', {
        controller:EditCtrl, 
        templateUrl:'detail.html',
        resolve: {
          team: function(Restangular, $route){
            return Restangular.one('teams', $route.current.params.teamUUID).get();
          }
        }
      }).
      when('/new', {controller:CreateCtrl, templateUrl:'detail.html'}).
      otherwise({redirectTo:'/'});
      
      RestangularProvider.setDefaultHeaders({'Authorization': 'Bearer ' + token});
      RestangularProvider.setBaseUrl('http://localhost:8080/application');
      RestangularProvider.setRestangularFields({
        id: 'uuid'
      });
      
      RestangularProvider.setRequestInterceptor(function(elem, operation, what) {
        return elem;
      });
      RestangularProvider.setResponseInterceptor(function(response, operation, what, url) {
        return response;
      });
      RestangularProvider.setErrorInterceptor(function(response) {
        if (response.status == '401') {
          console.log("Not authorized");
          window.location = '/static.myezteam.com/login.html';
          return;
        }
        return response;
      });
      
  });


function ListCtrl($scope, Restangular) {
  $scope.teams = Restangular.all('teams').getList();
}

function ViewCtrl($scope, Restangular, team) {
  $scope.team = team;
}


function CreateCtrl($scope, $location, Restangular) {
  $scope.save = function() {
    Restangular.all('teams').post($scope.team).then(function(team) {
      $location.path('/list');
    });
  }
}

function EditCtrl($scope, $location, Restangular, team) {
  var original = team;
  $scope.team = Restangular.copy(original);
  

  $scope.isClean = function() {
    return angular.equals(original, $scope.team);
  }

  $scope.destroy = function() {
    original.remove().then(function() {
      $location.path('/list');
    });
  };

  $scope.save = function() {
    $scope.team.put().then(function() {
      $location.path('/');
    });
  };
}