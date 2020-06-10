// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

// initialize body
function initBody() {
    hideLoginButton();
    initMap();
}

function showComments() {
    // where comments go
    var commentsContainer = document.getElementById('comments-container');
    commentsContainer.innerHTML = "";

    fetch('/data').then(response => response.json()).then((commentsJson) => {
        // unordered list
        var ul = document.createElement('ul');

        // add comments to container
        document.getElementById('comments-container').appendChild(ul);

        // iterate through json object and make into html list
        for (var key in commentsJson) {
            let li = document.createElement('li');
            li.innerHTML = commentsJson[key];
            ul.appendChild(li);
        }
        // BUG: currently appends all comments each time "show comments" button is clicked
    });
}

// redirect page to login upon "login/out" button click
function loginUser() {
    fetch('/login').then(response => response.json()).then(data => {
        window.location.replace(data.logInOutUrl);
    });

}

    // hide either logIn/logOut button according to fetched status
function hideLoginButton() {
    var loginButton = document.getElementById("login-button");
    var logoutButton = document.getElementById("logout-button");
    var commentForm = document.getElementById("comment-form");    

    // fetch and hide/show a button. none == hide
    fetch('/login').then(response => response.json()).then(data => {
        if (data.isLoggedIn) {
            loginButton.hidden = true;
            logoutButton.hidden = false;
            commentForm.hidden = false;
        } else {
            loginButton.hidden = false;
            logoutButton.hidden = true;
            commentForm.hidden = true;
        }
    });

}

// initalize map of Maryland
function initMap() {
    map = new google.maps.Map(document.getElementById('map'), {
        center: {lat: -34.397, lng: 76.6413},
        zoom: 10
    });

    const umdMarker = new google.maps.Marker({
        position: {lat: 38.9869, lng: 76.9426},
        map: map,
        title: 'University of Maryland'
    });

    const trexInfoWindow =
        new google.maps.InfoWindow({content: 'I\'m a student at the University of Maryland!'});
    trexInfoWindow.open(map, trexMarker);
}

