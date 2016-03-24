tourable.factory('classDataFactory', function() {
    return {
        admin: function (organization) {
            return {
                        expectedInput: [
                            {
                                description: "First Name",
                                model: "firstname",
                                type: "text",
                                help: "",
                                required: true
                            },
                            {
                                description: "Last Name",
                                model: "lastname",
                                type: "text",
                                help: "",
                                required: true
                            },
                            {
                                description: "Email",
                                model: "email",
                                type: "text",
                                help: "",
                                required: true
                            },
                            {
                                description: "Temporary Password",
                                model: "password",
                                type: "text",
                                help: "",
                                required: true
                            },
                            {
                                description: "Is this a super admin?",
                                model: "isSuper",
                                type: "boolean",
                                help: "Super admins can create admins and manage/edit every tour in an organization. They also have access to the dashboard.",
                                required: true
                            }
                        ],
                        defaultModels: {
                            organization: organization
                        },
                        afterCreate: {
                            route: "admin.manageAdmins",
                            options: {}
                        }
                    }
                },
        tour: function (admin) {
            return {
                        expectedInput: [
                            {
                                description: "Tour Title",
                                model: "title",
                                type: "text",
                                help: "Example: 'Cardiac Imaging Tour'",
                                required: true
                            },
                            {
                                description: "Tour Description",
                                model: "description",
                                type: "textarea",
                                help: "Don't worry, you will be able to edit and expand this later.",
                                required: true
                            }
                        ],
                        defaultModels: {
                            admin: admin.objectId,
                            isPublic : false, //expected input is a Boolean
                            estimatedTime: 30,
                            version: 1
                        },
                        afterCreate: {
                            route: "admin.manageTours",
                            options: {}
                        }
                    }
                },
        topSection: function (tour, depth) {
            return {
                        expectedInput: [
                            {
                                description: "Section Title",
                                model: "title",
                                type: "text",
                                help: "Example: 'MRI Room A'",
                                required: true
                            },
                            {
                                description: "Section Description",
                                model: "description",
                                type: "textarea",
                                help: "",
                                required: true
                            }
                        ],
                        defaultModels: {
                            tour: tour,
                            superSection: "",
                            depth: parseInt(depth)
                        },
                        afterCreate: {
                            route: "admin.edit.tour",
                            options: {
                                id: tour
                            }
                        }
                    }
                },
        section: function (tour, superSection, depth) {
            if (!superSection) {
                superSection = ""
            }
            return {
                        expectedInput: [
                            {
                                description: "Section Title",
                                model: "title",
                                type: "text",
                                help: "Example: 'MRI Room A'",
                                required: true
                            },
                            {
                                description: "Section Description",
                                model: "description",
                                type: "textarea",
                                help: "Don't worry, you will be able to edit and expand this later.",
                                required: false
                            }
                        ],
                        defaultModels: {
                            tour: tour,
                            superSection: superSection,
                            depth: parseInt(depth)
                        },
                        afterCreate: {
                            route: "admin.edit.section",
                            options: {
                                id: superSection
                            }
                        }
                    }
                },
        key: function (tour) {
            return {
                        expectedInput: [
                            {
                                description: "Key Code",
                                model: "code",
                                type: "text",
                                help: "Create a new key code, i.e. 'RBH-1010'",
                                required: true
                            },
                            {
                                description: "Expiry Date",
                                model: "expiry",
                                type: "date",
                                help: "Select the data and time when you want the key to expire.",
                                required: true
                            }
                        ],
                        defaultModels: {
                            tour: tour
                        },
                        afterCreate: {
                            route: "admin.edit.tour",
                            options: {
                                id: tour
                            }
                        }
                    }
                },
        poi: function (section) {
            return {
                        expectedInput: [
                            {
                                description: "POI Title",
                                model: "title",
                                type: "text",
                                help: "Example: 'Exercise machine A'",
                                required: true
                            }
                        ],
                        defaultModels: {
                            description: " ",
                            post: [],
                            section: section
                        },
                        afterCreate: {
                            route: "admin.edit.section",
                            options: {
                                id: section
                            }
                        }
                    }
                },
    };
});
