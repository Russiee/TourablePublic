tourable.filter('trustVideo', function ($sce) {
    return function(url) {
        return $sce.trustAsResourceUrl(url);
    };
});
