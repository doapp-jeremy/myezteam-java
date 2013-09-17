var token = localStorage.getItem("token");
angular.module('team', ['restangular']).
  config(function($routeProvider, RestangularProvider) {
    $routeProvider.
      when('/', {
        controller: function($scope, Restangular) {
          $scope['teams'] = Restangular.all('teams').getList()
        }, 
        templateUrl:'list.html'
      }).
      when('/:uuid', {
        controller: function($scope, Restangular, team) {
          $scope['team'] = team;
        },
        templateUrl:'view.html',
        resolve: {
          team: function(Restangular, $route) {
            return Restangular.one('teams', $route.current.params.uuid).get();
          }
        }
      }).
      when('/:uuid/events', {
        controller: function($scope, Restangular, team) {
          $scope['team'] = team;
          $scope.events = team.getList('events');
        },
        templateUrl:'events.html',
        resolve: {
          team: function(Restangular, $route) {
            return Restangular.one('teams', $route.current.params.uuid).get();
          }
        }
      }).
      when('/edit/:uuid', {
        controller: function($scope, $location, Restangular, team) {
          var original = team;
          $scope.team = Restangular.copy(original);

          $scope.isClean = function() {
            return angular.equals(original, $scope['team']);
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
        }, 
        templateUrl:'detail.html',
        resolve: {
          team: function(Restangular, $route){
            return Restangular.one('teams', $route.current.params.uuid).get();
          }
        }
      }).
      when('/new', {controller: function($scope, $location, Restangular) {
          $scope.save = function() {
            Restangular.all('teams').post($scope.team).then(function(team) {
              $location.path('/list');
            });
          }
        },
        templateUrl:'detail.html'
      }).
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
        if (response.status == 401) {
          if (confirm("You are not logged in, do you want to login?")) {
            window.location = '/login.html';
            return;
          }
        }
        else if  (response.status == 403) {
          alert('You are not authorized to access the requested data');
        }
        else if (response.status == 0) {
          alert('Did not get a response. Is the server running?');
        }
        else if (response.status >= 400) {
          alert('There was a server error');
        }
        return response;
      });
      
  });
