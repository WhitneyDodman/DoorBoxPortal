(function ($, MobileEsp) {
    // On document ready, redirect to the App on the App store.
    $(function () {
      if (typeof MobileEsp.DetectIos !== 'undefined' && MobileEsp.DetectIos()) {
        window.location = "http://portal.thedoorbox.com/iosWelcome.xhtml";
      } else if (typeof MobileEsp.DetectAndroid !== 'undefined' && MobileEsp.DetectAndroid()) {
        window.location = "http://portal.thedoorbox.com/androidWelcome.xhtml";
      } else {
        window.location = "http://portal.thedoorbox.com/genericWelcome.xhtml";
      }
    });
  })(jQuery, MobileEsp);
