$(function() {
  var currentUser = localStorage.getItem('email');
  console.log("Current user: " + currentUser);
  navigator.id.watch({
    loggedInUser: currentUser,
    onlogin: function(assertion) {
      // A user has logged in! Here you need to:
      // 1. Send the assertion to your backend for verification and to create a session.
      // 2. Update your UI.
      $.ajax({ /* <-- This example uses jQuery, but you can use whatever you'd like */
        type: 'POST',
        url: 'http://localhost:8080/application/auth/persona/login', // This is a URL on your website.
        data: JSON.stringify({assertion: assertion, audience: 'http://s3.amazonaws.com/'}),
        dataType: 'json',
        contentType: "application/json; charset=utf-8",
        success: function(res, status, xhr) { 
          alert("Login success!!");
          console.log("Email: " + res.email);
          console.log("Token: " + res.token);
          //window.location = '/application/users/' + res.token;
          localStorage.setItem('email',res.email);
          localStorage.setItem('token',res.token);
        },
        error: function(xhr, status, err) {
          navigator.id.logout();
          alert("Login failure: " + err.message);
        }
      });
    },
    onlogout: function() {
      console.log("Logged out");
    }
  });
});

var signinLink = document.getElementById('signin');
if (signinLink) {
  signinLink.onclick = function() { navigator.id.request(); };
}

var signoutLink = document.getElementById('signout');
if (signoutLink) {
  localStorage.removeItem('token');
  localStorage.removeItem('email');
  signoutLink.onclick = function() { navigator.id.logout(); };
}
