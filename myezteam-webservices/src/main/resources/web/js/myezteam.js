var currentUser = localStorage.getItem("email");
var token = localStorage.getItem("token");
var wsUrl = 'http://localhost:8080';

angular.module('myezteam',['restangular','ui.bootstrap']).
  config(function($routeProvider, RestangularProvider) {
    $routeProvider.
      when('/', { controller: TeamListCtrl, templateUrl: 'partials/team_list.html' }).
      when('/teams/view/:teamUUID', { resolve: { team: TeamResolver }, controller: TeamViewCtrl, templateUrl:'partials/team_view.html' }).
      when('/teams/edit/:teamUUID', { resolve: { team: TeamResolver }, controller: TeamEditCtrl, templateUrl:'partials/team_form.html' }).
      //when('/teams/events/:teamUUID', { resolve: { team: TeamResolver }, controller: EventListCtrl, templateUrl:'partials/event_list.html' }).
      otherwise({redirectTo:'/'})

    RestangularProvider.setDefaultHeaders({'Authorization': 'Bearer ' + token});
    RestangularProvider.setBaseUrl(wsUrl);
    RestangularProvider.setRestangularFields({ id: 'uuid' });
    
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

