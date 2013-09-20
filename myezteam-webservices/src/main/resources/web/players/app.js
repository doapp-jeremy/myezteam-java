var token = localStorage.getItem("token");
angular.module('player', ['restangular','ui.bootstrap']).
  config(function($routeProvider, RestangularProvider) {
    $routeProvider.
      when('/', {
        controller: function($scope, Restangular) {
          $scope.teams = Restangular.all('teams').getList();
          Restangular.all('players').getList().then(function(players){
            $scope.players = players;
            console.log($scope.players);
            var teamUUIDs = [];
            console.log(players.length);
            for (var i = 0; i < players.length; ++i) {
              console.log($scope.players[i]);
              teamUUIDs.push(players[i].team_uuid);
            }
            console.log(teamUUIDs);
            Restangular.all('teams',teamUUIDs).getList().then(function(teams){
              var teamsById = {};
              for (var i = 0; i < teams.length; ++i) {
                var team = teams[i];
                teamsById[team.uuid] = team;
              }
              $scope.teamsById = teamsById;
              console.log($scope.teamsById);
            });
          });
        }, 
        templateUrl:'list.html'
      }).
      otherwise({redirectTo:'/'});
      
      RestangularProvider.setDefaultHeaders({'Authorization': 'Bearer ' + token});
      RestangularProvider.setBaseUrl('http://localhost:8080/application');
      RestangularProvider.setRestangularFields({
        id: 'uuid'
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

function PlayerTypeCtrl($scope, Restangular) {
  $scope.player_types = [
                  {name:'Regular'},
                  {name:'Sub'},
                  {name:'Member'}
                  ];
  if (typeof $scope.player === 'undefined') {
    $scope.player = {  };
  }
  if (typeof $scope.player.player_type === 'undefined') {
    $scope.player.player_type = $scope.player_types[0].name;
  }
}

