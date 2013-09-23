function TeamResolver(Restangular, $route) {
  return Restangular.one('teams', $route.current.params.teamUUID).get();
}

function TeamListCtrl($scope, Restangular){
  return $scope.teams = Restangular.all('teams').getList();
}

function TeamViewCtrl($scope, Restangular, team) {
  $scope.team = team;
  // TODO: how to make this dynamic
  $scope.events = team.getList('events');
  $scope.players = team.getList('players');
}

function TeamEditCtrl($scope, $location, Restangular, team) {
  var original = team;
  $scope.team = Restangular.copy(original);

  $scope.isClean = function() {
    return angular.equals(original, $scope.team);
  }

  $scope.destroy = function() {
    original.remove().then(function() {
      $location.path('/teams/view/' + team.uuid);
    });
  };

  $scope.save = function() {
    $scope.team.put().then(function() {
      $location.path('/teams/view/' + team.uuid);
    });
  };
}
