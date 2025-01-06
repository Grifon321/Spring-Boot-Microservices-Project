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
            sap.ui.getCore().setModel(oModel);
        },

        onSignup: function () {
            // Load all the values
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
            fetch(window._env.BACKEND_URL + "/api/signup", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify(oData)
            })
            .then(response => {
                if (response.ok) {
                    MessageToast.show("Signup successful for " + sUsername + "!");
                    return response.json();
                } else if (response.status === 400) {
                    MessageToast.show("Bad Request: Username or password missing.");
                    return null;
                } else if (response.status === 401) {
                    MessageToast.show("Unauthorized: Invalid credentials.");
                    return null;
                } else {
                    MessageToast.show("An unknown error occurred. Please try again.");
                    return null;
                }
            })
            .catch(error => {
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
            fetch(window._env.BACKEND_URL + "/api/login", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify(oData)
            })
            .then(response => {
                if (response.ok) {
                    return response.json(); // Parse JSON response for the token
                } else if (response.status === 400) {
                    MessageToast.show("Bad Request: Username or password missing.");
                    return null;
                } else if (response.status === 401) {
                    MessageToast.show("Unauthorized: Invalid credentials.");
                    return null;
                } else {
                    MessageToast.show("An unknown error occurred. Please try again.");
                    return null;
                }
            })
            .then(data => {
                if (!data) return;

                if (data.token) {
                    // Store the token in sessionStorage
                    sessionStorage.setItem("authToken", data.token);
                    sessionStorage.setItem("username", sUsername);
                    MessageToast.show("Login successful for " + sUsername + "!");
                    this.onShowTasks();

                    // Hide login page
                    var oVbox = this.byId("login");
                    if (oVbox)
                        oVbox.setVisible(false);

                    // Show contents
                    var oVbox = this.byId("toDoContent");
                    if (oVbox)
                        oVbox.setVisible(true);

                    // Show log off button
                    var oVBox = this.byId("logOffButton");
                    if (oVBox)
                        oVBox.setVisible(true);

                    // Change title
                    var oPage = this.byId("loginSignupPage");
                    if (oPage)
                        oPage.setTitle("Welcome to your tasks!")

                    // Change username in the header
                    var oText = this.byId("usernameTextHeader");
                    if (oText) {
                        oText.setText("Username : " + sUsername);
                        oText.setVisible(true);
                    }

                } else {
                    throw new Error("Token missing in response");
                }
            })
            .catch(error => {
                console.error("Error:", error);
            });

        },

        onLogOff: function () {
            // Hide login page
            var oVbox = this.byId("login");
            if (oVbox)
                oVbox.setVisible(true);

            // Show contents
            var oVbox = this.byId("toDoContent");
            if (oVbox)
                oVbox.setVisible(false);

            // Show log off button
            var oVBox = this.byId("logOffButton");
            if (oVBox)
                oVBox.setVisible(false);

            // Change title
            var oPage = this.byId("loginSignupPage");
            if (oPage)
                oPage.setTitle("Login/Signup to your account")

            // Hide username in the header
            var oText = this.byId("usernameTextHeader");
            if (oText)
                oText.setVisible(false);

            // Clear the data
            var oModel = sap.ui.getCore().getModel();
            oModel.setProperty("/tasks", []);
        },

        onShowTasks:  function () {
            // Prepare the data to send (username from session storage)
            var sUsername = sessionStorage.getItem("username");
            var sToken = sessionStorage.getItem("authToken");
            // Check if the username and authToken exist
            if (!sUsername || !sToken) {
                console.error("Missing username or auth token");
                return;
            }

            // Retrieve data from the backend API using fetch()
            fetch(window._env.BACKEND_URL + `/api/content?username=${sUsername}&token=${sToken}`, {
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
                //var oModel = this.getView().getModel();
                var oModel = sap.ui.getCore().getModel();
                oModel.setProperty("/tasks", data);
            })
            .catch(error => {
                console.error("Error fetching protected resource:", error);
            });
        },

        onOpenAddTaskDialog: function () {
            var oDialog = this.byId("addNewTask");
            if (!oDialog) {
                var oDialog = sap.ui.xmlfragment(this.getView().getId(), "com.example.ui5app.view.AddNewTask", this);
                this.getView().addDependent(oDialog);
            }
            oDialog.open();
        },
        
        onCloseAddTaskDialog: function () {
            this.byId("addNewTask").close();
        },

        onAddTask: function () {
            // Load all the values
            var sUsername = sessionStorage.getItem("username");
            var sToken = sessionStorage.getItem("authToken");

            var oNameInput = this.byId("taskNameInput");
            var oTextInput = this.byId("taskTextInput");
            var oDeadlineInput = this.byId("taskDeadlineInput");
            var oSelect = this.byId("taskStatusSelect");
            var sStatus = oSelect.getSelectedKey();
            var sName = oNameInput.getValue();
            var sText = oTextInput.getValue();
            var sDeadline = oDeadlineInput.getValue();
            

            if (sName && sText && sToken && sUsername) {
                // Prepare the data to send
                var oData = {
                    token: sToken,
                    username: sUsername,
                    name: sName,
                    text: sText,
                    deadline: sDeadline,
                    status: sStatus
                };

                // Send data to the backend API using fetch()
                fetch(window._env.BACKEND_URL + "/api/createTask", {
                    method: "POST",
                    headers: {
                        "Content-Type": "application/json"
                    },
                    body: JSON.stringify(oData)
                })
                .then(response => {
                    if (response.status === 201) {
                        MessageToast.show("Task added successfully.");
                        this.onShowTasks();
                    } else if (response.status === 400) {
                        MessageToast.show("Bad Request: Username or password missing.");
                        return null;
                    } else {
                        MessageToast.show("An unknown error occurred. Please try again.");
                        return null;
                    }
                })
                .catch(error => {
                    console.error("Error:", error);
                });

                this.onCloseAddTaskDialog();
                
            } else {
                MessageToast.show("Please fill in both fields.");
            }
        },
        
        onSearch:  function () {
            // Prepare the data to send (username from session storage)
            var sUsername = sessionStorage.getItem("username");
            var sToken = sessionStorage.getItem("authToken");
            
            var oSearchbar = this.byId("searchField");
            var sQuery = oSearchbar.getValue();
            
            // Prevents clearing table on empty search
            if (!sQuery) {
                this.onShowTasks();
                return;
            }

            var sTransformedQuery = sQuery.replace(/\|\|/g, "%7C%7C");

            // Check if the username and authToken exist
            if (!sUsername || !sToken) {
                console.error("Missing username or auth token");
                return;
            }

            // Retrieve data from the backend API using fetch()
            fetch(window._env.BACKEND_URL + `/api/search?username=${sUsername}&token=${sToken}&query=${sTransformedQuery}`, {
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
                var oModel = sap.ui.getCore().getModel();
                oModel.setProperty("/tasks", data);
            })
            .catch(error => {
                console.error("Error fetching protected resource:", error);
            });
        },

        onHelpPress: function () {
            MessageToast.show('You can input AND and OR logic into the search via spacebar or || accordingly.');
        },

        // Displays the contents of the item, which triggered the function from context menu
        onShowTasksContext: function (oEvent) {
            var oSource = oEvent.getSource();
            var oItem = oSource.getParent().getBindingContext();
            
            // If no context is found
            if (!oItem) {
                MessageToast.show("No data found for the selected item.");
                return;
            }

            var oData = oItem.getObject();
            MessageToast.show(`ID: ${oData.id}\nName: ${oData.name}\nText: ${oData.text}\nDeadline: ${oData.deadline}\nStatus: ${oData.status}\nUser IDs : ${oData.userIds}`); // 
        },

        // Deletes the task, which triggered the function from context menu
        onDeleteTask: function (oEvent) {
            var oSource = oEvent.getSource();
            var oItem = oSource.getParent().getBindingContext();
            
            // If no context is found
            if (!oItem) {
                MessageToast.show("No data found for the selected item.");
                return;
            }

            // Prepare the id and params
            var oData = oItem.getObject();
            var nId = oData.id;
            var sToken = sessionStorage.getItem("authToken");

            if (sToken && nId) {
                // Prepare the data to send
                var oData = {
                    token: sToken,
                    id: nId
                };

                // Send data to the backend API using fetch()
                fetch(window._env.BACKEND_URL + "/api/deleteTask", {
                    method: "DELETE",
                    headers: {
                        "Content-Type": "application/json"
                    },
                    body: JSON.stringify(oData)
                })
                .then(response =>  {
                    if (response.status === 204) {
                        MessageToast.show("Task deleted successfully");
                        this.onShowTasks();
                    } else if (response.status === 400) {
                        MessageToast.show("Bad Request.");
                        return null;
                    } else if (response.status === 401) {
                        MessageToast.show("Unauthorized: Invalid credentials.");
                        return null;
                    } else {
                        MessageToast.show("An unknown error occurred. Please try again.");
                        return null;
                    }
                })
                .catch(error => {
                    console.error("Error:", error);
                });
            } else {
                MessageToast.show("Token does not exist or task has no id");
            }
        },

        onMoveToStatus: function (oEvent, oStatus) {
            var oSource = oEvent.getSource();
            var oItem = oSource.getParent().getBindingContext();
            
            // If no context is found
            if (!oItem) {
                MessageToast.show("No data found for the selected item.");
                return;
            }

            // Prepare the id and params
            var sToken = sessionStorage.getItem("authToken");
            var oData = oItem.getObject();
            var nId = oData.id;
            var sName = oData.name;
            var sText = oData.text;
            var sDeadline = oData.deadline;
            var aUserIds = oData.userIds;
            

            if (sToken && nId) {
                // Prepare the data to send
                var oData = {
                    token: sToken,
                    id: nId,
                    name: sName,
                    text: sText,
                    deadline: sDeadline,
                    status: oStatus,
                    userIds: aUserIds
                };

                // Send data to the backend API using fetch()
                fetch(window._env.BACKEND_URL + "/api/editTask", {
                    method: "PUT",
                    headers: {
                        "Content-Type": "application/json"
                    },
                    body: JSON.stringify(oData)
                })
                .then(response => {
                    if (response.status === 200) {
                        MessageToast.show("Task moved successfully");
                        this.onShowTasks();
                    } else if (response.status === 400) {
                        MessageToast.show("Bad Request: Username or password missing.");
                    } else if (response.status === 401) {
                        MessageToast.show("Unauthorized: Invalid credentials.");
                    } else {
                        MessageToast.show("An unknown error occurred. Please try again.");
                    }
                })
                .catch(error => {
                    console.error("Error:", error);
                });
            } else {
                MessageToast.show("Token does not exist or task has no id");
            }
        },

        onMoveToToDo: function (oEvent) {
            this.onMoveToStatus(oEvent, "To Do");
        },

        onMoveToInProgress: function (oEvent) {
            this.onMoveToStatus(oEvent, "In Progress");
        },

        onMoveToDone: function (oEvent) {
            this.onMoveToStatus(oEvent, "Done");
        },

        onEditTask: function (oEvent) {
            var oSource = oEvent.getSource();
            var oItem = oSource.getParent().getBindingContext();
        
            // If no context is found
            if (!oItem) {
                MessageToast.show("No data found for the selected item.");
                return;
            }
        
            // Prepare the id and params
            var sToken = sessionStorage.getItem("authToken");
            var oData = oItem.getObject();
            var nId = oData.id;
            var sName = oData.name;
            var sText = oData.text;
            var sDeadline = oData.deadline;
            var sStatus = oData.status;
            var aUserIds = oData.userIds;
        
            // Open the dialog to change task details
            var oDialog = this.byId("editTask");
            if (!oDialog) {
                oDialog = sap.ui.xmlfragment(this.getView().getId(), "com.example.ui5app.view.EditTask", this);
                this.getView().addDependent(oDialog);
            }
            // Preload the task's details
            this.byId("editTaskNameInput").setValue(sName);
            this.byId("editTaskTextInput").setValue(sText);
            this.byId("editTaskDeadlineInput").setValue(sDeadline);
            this.byId("editTaskStatusLabel").setText("Current status is: " + sStatus);
            oDialog.open();

            // Store task id and userIds for later use
            this._currentTaskId = nId;
            this._currentTaskUserIds = aUserIds;
        },

        onEditTaskDialog: function () {
            // Load all the values
            var sName = this.byId("editTaskNameInput").getValue();
            var sText = this.byId("editTaskTextInput").getValue();
            var sDeadline = this.byId("editTaskDeadlineInput").getValue();
            var oSelect = this.byId("editTaskStatusSelect");
            var sStatus = oSelect.getSelectedKey();
            var nId = this._currentTaskId;
            var aUserIds = this._currentTaskUserIds;
            var sToken = sessionStorage.getItem("authToken");
        
            if (sToken && nId) {
                var oData = {
                    token: sToken,
                    id: nId,
                    name: sName,
                    text: sText,
                    deadline: sDeadline,
                    status: sStatus,
                    userIds: aUserIds
                };
        
                // Send data to the backend API using fetch()
                fetch(window._env.BACKEND_URL + "/api/editTask", {
                    method: "PUT",
                    headers: {
                        "Content-Type": "application/json"
                    },
                    body: JSON.stringify(oData)
                })
                .then(response => {
                    if (response.status === 200) {
                        MessageToast.show("Task moved successfully");
                        this.onShowTasks();
                    } else if (response.status === 400) {
                        MessageToast.show("Bad Request: Username or password missing.");
                    } else if (response.status === 401) {
                        MessageToast.show("Unauthorized: Invalid credentials.");
                    } else {
                        MessageToast.show("An unknown error occurred. Please try again.");
                    }
                })
                .catch(error => {
                    console.error("Error:", error);
                });
            } else {
                MessageToast.show("Token does not exist or task has no id");
            }
        
            // Close the dialog after edit
            this.byId("editTask").close();
        },
        
        onCloseEditTaskDialog: function () {
            this.byId("editTask").close();
        },

        onUpdateUser: function(oEvent) {
            var oSource = oEvent.getSource();
            var oItem = oSource.getParent().getBindingContext();
        
            // If no context is found
            if (!oItem) {
                MessageToast.show("No data found for the selected item.");
                return;
            }
        
            // Prepare the id and params
            var oData = oItem.getObject();
            var nId = oData.id;
            var aUserIds = oData.userIds;
        
            // Open the dialog to change task details
            var oDialog = this.byId("updateUsers");
            if (!oDialog) {
                oDialog = sap.ui.xmlfragment(this.getView().getId(), "com.example.ui5app.view.UpdateUsers", this);
                this.getView().addDependent(oDialog);
            }
            // Preload the task's details
            this.byId("updateUsersLabel").setText("Current users are: " + aUserIds);
            oDialog.open();

            // Store task id and user ids for later use
            this._currentTaskId = nId;
        },

        onUpdateUsersDialog: function () {
            // Load all the values
            var userId = this.byId("updateUsersInput").getValue().trim();
            var nTransformedUserId = null;
            var nId = this._currentTaskId;
            var sToken = sessionStorage.getItem("authToken");
            var oSelect = this.byId("updateUsersSelect");
            var sSelectedKey = oSelect.getSelectedKey();
            var sRequestPath = null;
            
            // Check operation
            if (sSelectedKey == "add")
                sRequestPath = "addUserId";
            else if (sSelectedKey == "remove")
                sRequestPath = "removeUserId";
            else {
                MessageToast.show("Select the operation please");
                return;
            }
            
            // Check if user id is a number or transform
            if (!isNaN(userId)) {
                nTransformedUserId = userId;
            } else {
                nTransformedUserId = parseInt(userId, 10);
                if (isNaN(nTransformedUserId)) {
                    MessageToast.show("Invalid User ID, please enter a numeric value");
                    return;
                }
            }
        
            if (sToken && nId && userId) {
                // Prepare data
                var oData = {
                    token: sToken,
                    id: nId,
                    userId: nTransformedUserId
                };

                // Send data to the backend API using fetch()
                fetch(window._env.BACKEND_URL + "/api/" + sRequestPath, {
                    method: "POST",
                    headers: {
                        "Content-Type": "application/json"
                    },
                    body: JSON.stringify(oData)
                })
                .then(response => {
                    if (response.status === 200) {
                        MessageToast.show("Task edited successfully");
                        this.onShowTasks();
                    } else if (response.status === 400) {
                        MessageToast.show("Bad Request: Username or password missing.");
                    } else if (response.status === 401) {
                        MessageToast.show("Unauthorized: Invalid credentials.");
                    } else {
                        MessageToast.show("An unknown error occurred. Please try again.");
                    }
                })
                .catch(error => {
                    console.error("Error:", error);
                });
            } else {
                MessageToast.show("Token does not exist, task has no id or user id is not present");
            }
        
            // Close the dialog after edit
            this.byId("updateUsers").close();
        },
        
        onCloseUpdateUsersDialog: function () {
            this.byId("updateUsers").close();
        },
        
    });
});
