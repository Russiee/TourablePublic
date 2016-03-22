tourable.factory('classDataFactory', function() {
    return {
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
                                type: "textarea-small",
                                help: "Don't worry, you will be able to edit and expand this later.",
                                required: false
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
                                type: "textarea-small",
                                help: "Don't worry, you will be able to edit and expand this later.",
                                required: false
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
                                type: "textarea-small",
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
                }
    };
});
