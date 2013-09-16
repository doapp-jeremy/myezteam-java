var token = localStorage.getItem('token');
var config = {headers:  {
        'Authorization': 'Bearer ' + token,
        'Accept': 'application/json'
    }
};

module.factory('Team', function($resource){
  
});
