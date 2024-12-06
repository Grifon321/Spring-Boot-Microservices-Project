sap.ui.define([
    "sap/ui/core/mvc/Controller",
    "sap/ui/model/json/JSONModel",
    "sap/ui/core/Fragment",
    "sap/m/MessageToast"
], function (Controller, JSONModel, Fragment, MessageToast) {
    "use strict";

    return Controller.extend("com.example.ui5app.controller.App", {
        onInit: function () {
            var oModel = new JSONModel();
            this.getView().setModel(oModel);
        },

        onSignup: function () {
            var oView = this.getView();
            var sUsername = oView.byId("usernameInput").getValue();
            var sEmail = oView.byId("emailInput").getValue();
            var sPassword = oView.byId("passwordInput").getValue();

            if (!sUsername || !sPassword) {
                MessageToast.show("Please enter both username and password.");
                return;
            }

            // Prepare the data to send
            var oData = {
                username: sUsername,
                email: sEmail,
                password: sPassword
            };

            // Send data to the backend API using fetch()
            fetch("http://localhost:8089/api/signup", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify(oData)
            })
            .then(response => {
                if (response.ok) {
                    MessageToast.show("Signup succeeded.");
                    return response.json();
                } else {
                    MessageToast.show("Signup failed. Please try again.");
                }
            })
            .catch(error => {
                MessageToast.show("Error signing up user.");
                console.error("Error:", error);
            });
        },

        onLogin: function () {
            // Get the input values
            var oView = this.getView();
            var sUsername = oView.byId("usernameInput").getValue();
            var sPassword = oView.byId("passwordInput").getValue();

            // Process or validate the inputs
            if (!sUsername || !sPassword) {
                MessageToast.show("Please enter both username and password.");
                return;
            }

            // Prepare the data to send
            var oData = {
                username: sUsername,
                password: sPassword
            };

            // Send data to the backend API using fetch()
            fetch("http://localhost:8089/api/login", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify(oData)
            })
            .then(response => {
                if (response.ok) {
                    return response.json(); // Parse JSON response for the token
                } else {
                    MessageToast.show("Login failed. Please try again.");
                }
            })
            .then(data => {
                if (data.token) {
                    // Store the token in sessionStorage
                    sessionStorage.setItem("authToken", data.token);
                    sessionStorage.setItem("username", sUsername);
                    MessageToast.show("Login successful for " + sUsername + "!");

                    // Hide login page, show contents, rename page
                    var oVbox = this.byId("login");
                    if (oVbox)
                        oVbox.setVisible(false);
                    var oVbox = this.byId("content");
                    if (oVbox)
                        oVbox.setVisible(true);
                    var oPage = this.byId("loginSignupPage");
                    if (oPage)
                        oPage.setTitle("Welcome to your tasks!")

                } else {
                    throw new Error("Token missing in response");
                }
            })
            .catch(error => {
                MessageToast.show("Error logging user.");
                console.error("Error:", error);
            });
        },

        onLogOff: function () {
            // Hide login panel and show body panel
            var oVbox = this.byId("login");
            if (oVbox)
                oVbox.setVisible(true);
            var oVbox = this.byId("content");
            if (oVbox)
                oVbox.setVisible(false);
            var oPage = this.byId("loginSignupPage");
            if (oPage)
                oPage.setTitle("Login/Signup to your account")
            // Clear the data
            var oModel = this.getView().getModel();
            oModel.setProperty("/tasks", []);
        },

        onShowTasks:  function () {
            // Prepare the data to send (username from session storage)
            var username = sessionStorage.getItem("username");
            var token = sessionStorage.getItem("authToken");
            // Check if the username and authToken exist
            if (!username || !token) {
                console.error("Missing username or auth token");
                return;
            }

            // Retrieve data from the backend API using fetch()
            fetch(`http://localhost:8089/api/content?username=${username}&token=${token}`, {
                method: "GET"
            })
            .then(response => {
                if (response.ok) {
                    return response.json();
                } else {
                    throw new Error("Request failed");
                }
            })
            .then(data => {
                var oModel = this.getView().getModel();
                
                oModel.setProperty("/tasks", data);
                this.getView().byId("content").setVisible(true);
                this.getView().byId("tasksList").setVisible(true);
            })
            .catch(error => {
                console.error("Error fetching protected resource:", error);
            });
        },

        onOpenDialog: function () {
            var oDialog = this.byId("addNewTask");
            if (!oDialog) {
                var oDialog = sap.ui.xmlfragment(this.getView().getId(), "com.example.ui5app.view.AddNewTask", this);
                this.getView().addDependent(oDialog);
            }
            oDialog.open();
        },
        
        onCloseDialog: function () {
            this.byId("addNewTask").close();
        },

        onAddTask: function () {
            var username = sessionStorage.getItem("username");
            var token = sessionStorage.getItem("authToken");

            var oNameInput = this.byId("taskNameInput");
            var oTextInput = this.byId("taskTextInput");
            var sName = oNameInput.getValue();
            var sText = oTextInput.getValue();
            

            if (sName && sText && token && username) {
                // Prepare the data to send
                var oData = {
                    token: token,
                    username: username,
                    name: sName,
                    text: sText,
                };

                // Send data to the backend API using fetch()
                fetch("http://localhost:8089/api/createTask", {
                    method: "POST",
                    headers: {
                        "Content-Type": "application/json"
                    },
                    body: JSON.stringify(oData)
                })
                .then(response => {
                    if (response.ok) {
                        return response.json();
                    } else {
                        MessageToast.show("Login failed. Please try again.");
                    }
                })
                .catch(error => {
                    MessageToast.show("Error adding data");
                    console.error("Error:", error);
                });

                MessageToast.show("New task added: " + sName + " - " + sText);
                this.onCloseDialog();
            } else {
                MessageToast.show("Please fill in both fields.");
            }
        },

        onSearch:  function () {
            // Prepare the data to send (username from session storage)
            var username = sessionStorage.getItem("username");
            var token = sessionStorage.getItem("authToken");
            
            var oSearchbar = this.byId("searchField");
            var query = oSearchbar.getValue();

            // Check if the username and authToken exist
            if (!username || !token) {
                console.error("Missing username or auth token");
                return;
            }

            // Retrieve data from the backend API using fetch()
            fetch(`http://localhost:8089/api/search?username=${username}&token=${token}&query=${query}`, {
                method: "GET"
            })
            .then(response => {
                if (response.ok) {
                    return response.json();
                } else {
                    throw new Error("Request failed");
                }
            })
            .then(data => {
                var oModel = this.getView().getModel();
                
                oModel.setProperty("/tasks", data);
                this.getView().byId("content").setVisible(true);
                this.getView().byId("tasksList").setVisible(true);
            })
            .catch(error => {
                console.error("Error fetching protected resource:", error);
            });
        },

    });
});
