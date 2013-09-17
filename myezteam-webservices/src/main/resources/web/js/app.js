var token = localStorage.getItem("token");
angular.module('team', ['restangular']).
  config(function($routeProvider, RestangularProvider) {
    $routeProvider.
      when('/', {
        controller:ListCtrl, 
        templateUrl:'list.html'
      }).
      when('/:teamUUID', {
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
        id: 'uuid.$oid'
      });
      
      RestangularProvider.setRequestInterceptor(function(elem, operation, what) {
//        if (operation === 'put') {
//          elem.uuid = undefined;
//          return elem;
//        }
        return elem;
      })
  });


function ListCtrl($scope, Restangular) {
  $scope.teams = Restangular.all('teams').getList();
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