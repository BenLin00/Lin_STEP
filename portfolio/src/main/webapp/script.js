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

/**
 * Adds a random greeting to the page.
 */
function addRandomGreeting() {
  const greetings =
      ['Hello world!', '¡Hola Mundo!', '你好，世界！', 'Bonjour le monde!'];

  // Pick a random greeting.
  const greeting = greetings[Math.floor(Math.random() * greetings.length)];

  // Add it to the page.
  const greetingContainer = document.getElementById('greeting-container');
  greetingContainer.innerText = greeting;
}


function showComments() {
    fetch('/data').then(response => response.json()).then((commentsJson) => {
     
        // unordered list
        var ul = document.createElement('ul');

        // where comments go
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
function loginButton() {
    fetch('/login').then(response => response.json()).then(data => {
        window.location.replace(data.logInOutUrl);
    });

    hideLoginButton(); // this function call is redundant because line 53 triggers hideLoginButton() onload
}

    // hide either logIn/logOut button according to fetched status
function hideLoginButton() {
    var loginButton = document.getElementById("login-button");
    var logoutButton = document.getElementById("logout-button");
    var commentForm = document.getElementById("comment-form");

    // fetch and hide/show a button. none == hide
    fetch('/login').then(response => response.json()).then(data => {
        if (data.isLoggedIn) {
            loginButton.style.display = "none";
            logoutButton.style.display = "inline";
            commentForm.style.display = "inline";
        } else {
            loginButton.style.display = "inline";
            logoutButton.style.display = "none";
            commentForm.style.display = "none";
        }
    });

    

}
