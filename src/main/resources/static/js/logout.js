/*const logoutBtn = document.querySelector(".logout-btn")

logoutBtn.addEventListener("click",()=>{
    window.location.replace("/login")
})*/


/* $(function() {

    Parse.$ = jQuery;
Parse.initialize("MY CODE HERE", "MY CODE HERE");

    $('.form-logout').on('submit', function(e) {

    // Prevent Default Submit Event
    e.preventDefault();

    //logout current user
    var currentUser = Parse.User.current();
        if (currentUser) {
            Parse.User.logout();
            window.location="/login";
        } else {
            window.location="/login";
        }

    });

});*/

/*function logout() {
    (function(myCallbackGoesHereAsVariable) {
      Parse.User.logOut();
    })(myFunctionToShowTheLoginScreen())

  }*/
function myFunction() 
{
      window.location.href = "/loggedout";
      alert("Logout Successfully, Thank You !!!")
    
}
  