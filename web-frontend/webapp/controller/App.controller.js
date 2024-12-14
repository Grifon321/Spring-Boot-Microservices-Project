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
                MessageToast.show("Error logging user.");
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
            var username = sessionStorage.getItem("username");
            var token = sessionStorage.getItem("authToken");

            var oNameInput = this.byId("taskNameInput");
            var oTextInput = this.byId("taskTextInput");
            var oDeadlineInput = this.byId("taskDeadlineInput");
            var oSelect = this.byId("taskStatusSelect");
            var sStatus = oSelect.getSelectedKey();
            var sName = oNameInput.getValue();
            var sText = oTextInput.getValue();
            var sDeadline = oDeadlineInput.getValue();
            

            if (sName && sText && token && username) {
                // Prepare the data to send
                var oData = {
                    token: token,
                    username: username,
                    name: sName,
                    text: sText,
                    deadline: sDeadline,
                    status: sStatus
                };

                // Send data to the backend API using fetch()
                fetch("http://localhost:8089/api/createTask", {
                    method: "POST",
                    headers: {
                        "Content-Type": "application/json"
                    },
                    body: JSON.stringify(oData)
                })
                .then(response => response.json())
                .then(data => {
                    if (data.success) {
                        MessageToast.show("Task added successfully");
                        this.onShowTasks();
                    } else {
                        MessageToast.show("Task added unsuccessfully");
                    }
                })
                .catch(error => {
                    MessageToast.show("Error adding data");
                    console.error("Error:", error);
                });

                this.onCloseAddTaskDialog();
                
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
            
            // Prevents clearing table on empty search
            if (!query) {
                this.onShowTasks();
                return;
            }

            var transformedQuery = query.replace(/\|\|/g, "%7C%7C");

            // Check if the username and authToken exist
            if (!username || !token) {
                console.error("Missing username or auth token");
                return;
            }

            // Retrieve data from the backend API using fetch()
            fetch(`http://localhost:8089/api/search?username=${username}&token=${token}&query=${transformedQuery}`, {
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
            var id = oData.id;
            var token = sessionStorage.getItem("authToken");

            if (token && id) {
                // Prepare the data to send
                var oData = {
                    token: token,
                    id: id
                };

                // Send data to the backend API using fetch()
                fetch("http://localhost:8089/api/deleteTask", {
                    method: "POST",
                    headers: {
                        "Content-Type": "application/json"
                    },
                    body: JSON.stringify(oData)
                })
                .then(response => response.json())
                .then(data => {
                    if (data.success) {
                        MessageToast.show("Task deleted successfully");
                        this.onShowTasks();
                    } else {
                        MessageToast.show("Task deleted unsuccessfully");
                    }
                })
                .catch(error => {
                    MessageToast.show("Error adding data");
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
            var token = sessionStorage.getItem("authToken");
            var oData = oItem.getObject();
            var id = oData.id;
            var name = oData.name;
            var text = oData.text;
            var deadline = oData.deadline;
            var userIds = oData.userIds;
            

            if (token && id) {
                // Prepare the data to send
                var oData = {
                    token: token,
                    id: id,
                    name: name,
                    text: text,
                    deadline: deadline,
                    status: oStatus,
                    userIds: userIds
                };

                // Send data to the backend API using fetch()
                fetch("http://localhost:8089/api/editTask", {
                    method: "POST",
                    headers: {
                        "Content-Type": "application/json"
                    },
                    body: JSON.stringify(oData)
                })
                .then(response => response.json())
                .then(data => {
                    if (data.success) {
                        MessageToast.show("Task moved successfully");
                        this.onShowTasks();
                    } else {
                        MessageToast.show("Task moved unsuccessfully");
                    }
                })
                .catch(error => {
                    MessageToast.show("Error moving task");
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
            var token = sessionStorage.getItem("authToken");
            var oData = oItem.getObject();
            var id = oData.id;
            var name = oData.name;
            var text = oData.text;
            var deadline = oData.deadline;
            var status = oData.status;
            var userIds = oData.userIds;
        
            // Open the dialog to change task details
            var oDialog = this.byId("editTask");
            if (!oDialog) {
                oDialog = sap.ui.xmlfragment(this.getView().getId(), "com.example.ui5app.view.EditTask", this);
                this.getView().addDependent(oDialog);
            }
            // Preload the task's details
            this.byId("editTaskNameInput").setValue(name);
            this.byId("editTaskTextInput").setValue(text);
            this.byId("editTaskDeadlineInput").setValue(deadline);
            this.byId("editTaskStatusLabel").setText("Current status is: " + status);
            oDialog.open();

            // Store task id and userIds for later use
            this._currentTaskId = id;
            this._currentTaskUserIds = userIds;
        },

        onEditTaskDialog: function () {
            // Load all the values
            var name = this.byId("editTaskNameInput").getValue();
            var text = this.byId("editTaskTextInput").getValue();
            var deadline = this.byId("editTaskDeadlineInput").getValue();
            var oSelect = this.byId("editTaskStatusSelect");
            var status = oSelect.getSelectedKey();
            var id = this._currentTaskId;
            var userIds = this._currentTaskUserIds;
            var token = sessionStorage.getItem("authToken");
        
            if (token && id) {
                var oData = {
                    token: token,
                    id: id,
                    name: name,
                    text: text,
                    deadline: deadline,
                    status: status,
                    userIds: userIds
                };
        
                // Send data to the backend API using fetch()
                fetch("http://localhost:8089/api/editTask", {
                    method: "POST",
                    headers: {
                        "Content-Type": "application/json"
                    },
                    body: JSON.stringify(oData)
                })
                .then(response => response.json())
                .then(data => {
                    if (data.success) {
                        MessageToast.show("Task updated successfully");
                        this.onShowTasks();  // Reload the task list or update the UI
                    } else {
                        MessageToast.show("Task update failed");
                    }
                })
                .catch(error => {
                    MessageToast.show("Error updating task");
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
            var id = oData.id;
            var userIds = oData.userIds;
        
            // Open the dialog to change task details
            var oDialog = this.byId("updateUsers");
            if (!oDialog) {
                oDialog = sap.ui.xmlfragment(this.getView().getId(), "com.example.ui5app.view.UpdateUsers", this);
                this.getView().addDependent(oDialog);
            }
            // Preload the task's details
            this.byId("updateUsersLabel").setText("Current users are: " + userIds);
            oDialog.open();

            // Store task id and user ids for later use
            this._currentTaskId = id;
        },

        onUpdateUsersDialog: function () {
            // Load all the values
            var userId = this.byId("updateUsersInput").getValue().trim();
            var transformedUserId = null;
            var id = this._currentTaskId;
            var token = sessionStorage.getItem("authToken");
            var oSelect = this.byId("updateUsersSelect");
            var selectedKey = oSelect.getSelectedKey();
            var requestPath = null;
            
            // Check operation
            if (selectedKey == "add")
                requestPath = "addUserId";
            else if (selectedKey == "remove")
                requestPath = "removeUserId";
            else {
                MessageToast.show("Select the operation please");
                return;
            }
            
            // Check if user id is a number or transform
            if (!isNaN(userId)) {
                transformedUserId = userId;
            } else {
                transformedUserId = parseInt(userId, 10);
                if (isNaN(transformedUserId)) {
                    MessageToast.show("Invalid User ID, please enter a numeric value");
                    return;
                }
            }
        
            if (token && id && userId) {
                // Prepare data
                var oData = {
                    token: token,
                    id: id,
                    userId: transformedUserId
                };

                // Send data to the backend API using fetch()
                fetch("http://localhost:8089/api/" + requestPath, {
                    method: "PUT",
                    headers: {
                        "Content-Type": "application/json"
                    },
                    body: JSON.stringify(oData)
                })
                .then(response => response.json())
                .then(data => {
                    if (data.success) {
                        MessageToast.show("Operation successfull");
                        this.onShowTasks();  
                    } else {
                        MessageToast.show("Operation failed");
                    }
                })
                .catch(error => {
                    MessageToast.show("Error updating user id");
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
