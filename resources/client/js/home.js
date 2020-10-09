function pageLoad() {

    document.getElementById("getSession").addEventListener("click", getSessionCode);
}


function getSessionCode(){
    console.log("Fetching session code");

    const SessionID = document.getElementById("SessionID").value;
    const url = "/Sessions/get/";

    fetch(url + SessionID, {
        method: "GET",
    }).then(response => {
        return response.json();                 //return response to JSON
    }).then(response => {
        if (response.hasOwnProperty("Error")) { //checks if response from server has a key "Error"
            alert(JSON.stringify(response));    // if it does, convert JSON object to string and alert
        } else {
            //this function will create an HTML table of the data (as we
            // did in lesson 2
        }
    });

}