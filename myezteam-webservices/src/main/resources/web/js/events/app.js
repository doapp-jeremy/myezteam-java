var token = localStorage.getItem("token");
angular.module('event', ['restangular']).
  config(function($routeProvider, RestangularProvider) {
    $routeProvider.
      when('/team/:teamUUID', {
        controller: function($scope, Restangular, team, events) {
          $scope['team'] = team;
          $scope['events'] = events;
        },
        templateUrl:'list.html',
        resolve: {
          team: function(Restangular, $route) {
            return Restangular.one('teams', $route.current.params.teamUUID).get();
          },
          events: function(Restangular, $route) {
            return Restangular.all('events').getList();
          }
        }
      }).
      when('/team/:teamUUID/new', {controller: function($scope, $location, Restangular, team) {
          $scope.save = function() {
            Restangular.all('events').post($scope.event).then(function(event, team) {
              $location.path('/team/' + team.uuid);
            });
          }
        },
        templateUrl:'detail.html',
        resolve: {
          team: function(Restangular, $route) {
            return Restangular.one('teams', $route.current.params.teamUUID).get();
          }
        }
      }).
      otherwise({redirectTo:'/events'});
      
      RestangularProvider.setDefaultHeaders({'Authorization': 'Bearer ' + token});
      RestangularProvider.setBaseUrl('http://localhost:8080/application');
      RestangularProvider.setRestangularFields({ id: 'uuid' });
      
      RestangularProvider.setRequestInterceptor(function(elem, operation, what) {
        return elem;
      });
      RestangularProvider.setResponseInterceptor(function(response, operation, what, url) {
        return response;
      });
      RestangularProvider.setErrorInterceptor(function(response) {
        if (response.status == 401) {
          if (confirm("You are not logged in, do you want to login?")) {
            window.location = '/static.myezteam.com/login.html';
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
