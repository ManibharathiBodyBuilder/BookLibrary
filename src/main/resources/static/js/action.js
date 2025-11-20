
/*function login(){
	var email=document.getElement
}
*/

/*const form = document.querySelector("form")

form.addEventListener("submit",(e)=>{
    e.preventDefault()

    const username = form.username.value
    const password = form.password.value

    const authenticated = authentication(username,password)

    if(authenticated){
        window.location.href = "/testing"
    }else{
        alert("wrong")
    }
})

// function for checking username and password

function authentication(username,password){
    if(username == "2575715" && password == "787898"){
        return true
    }else{
        return false
    }
}*/
function myFunction()
{
var un = document.forms["myForm"]["Uname"].value;
var pw = document.forms["myForm"]["Pass"].value;
if(un=="Manibharathi" && pw==787898){
window.location.href="/Verified";
alert("Hi,"+un+", Sign in Successfully")
}
else{
alert("Invalid UserName and Password")
}
}
