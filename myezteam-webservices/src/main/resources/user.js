var config = {headers: {
            'Authorization': 'Bearer 81a1716f-b590-471b-8230-f1bc5d6fd469',
            'Accept': 'application/json'
        }
    };

$(function() {
  $http.get('http://localhost:8080/users/me', config).success(function(user){
    console.log(user);
  }).error(function(error){
    console.log(error);
  });
});
