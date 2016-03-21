tourable.factory('classDataFactory', function() {
    return {
        data: function(admin) {
            console.log("hahah", admin);
            return {
                tour: {
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
                            help: "",
                            required: false
                        }
                    ],
                    defaultModels: {
                        admin: admin.objectId,
                        isPublic : false, //expected input is a Boolean
                        estimatedTime: 30,
                        version: 1
                    },
                    afterCreate: "admin.manageTours"
                }
            };
        }
    };
});
