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
      when('/events/edit/:uuid', {
        controller: function($scope, $location, Restangular, event) {
          $scope.team = Restangular.one('teams', event.team_uuid).get();
          var original = event;
          $scope.event = Restangular.copy(original);

          $scope.isClean = function() {
            return angular.equals(original, $scope['event']);
          }

          $scope.destroy = function() {
            original.remove().then(function() {
              $location.path('/' + event.team_uuid + "/events");
            });
          };

          $scope.save = function() {
            $scope.event.put().then(function() {
              $location.path('/' + event.team_uuid + "/events");
            });
          };
        }, 
        templateUrl:'event_detail.html',
        resolve: {
          event: function(Restangular, $route){
            return Restangular.one('events', $route.current.params.uuid).get();
          }
        }
      }).
      when('/:uuid/events/new', {controller: function($scope, $location, Restangular, team) {
          $scope.team = team;
          $scope.save = function() {
            $scope.event.team_uuid = $scope.team.uuid;
            // I'm not really sure why angular doesn't do this for me when creating a new one
            var deafultRSVP = document.getElementById("newEventDefaultRSVP");
            $scope.event.default_rsvp = deafultRSVP.options[deafultRSVP.selectedIndex].text;
            console.log($scope.event);
            Restangular.all('events').post($scope.event).then(function(event) {
              $location.path('/' + team.uuid + "/events");
            });
          }
        },
        templateUrl:'event_detail.html',
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

function RsvpCtrl($scope, Restangular) {
  $scope.rsvps = [
                  {name:'No Response'},
                  {name:'Yes'},
                  {name:'Probable'},
                  {name:'Maybe'},
                  {name:'No'},
                  ];
  if (typeof $scope.event === 'undefined') {
    $scope.event = {  };
  }
  if (typeof $scope.event.default_rsvp === 'undefined') {
    $scope.event.default_rsvp = $scope.rsvps[0].name;
  }
}