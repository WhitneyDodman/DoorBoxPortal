window.timber = window.timber || {};

timber.cacheSelectors = function () {
  timber.cache = {
    // General
    $window             : $(window),
    $html               : $('html'),
    $body               : $('body'),

    // Breakpoints (from timber.scss.liquid)
    mediaQuerySmall     : 'screen and (max-width: 480px)',
    mediaQueryMedium    : 'screen and (min-width: 480px) and (max-width: 768px)',
    mediaQueryLarge     : 'screen and (min-width: 769px)',

    //scroll position
    scrollPosition      : 0,
    isAjaxCartVisible   : false,

    // Navigation
    $navigation         : $('#accessibleNav'),
    $menuToggle         : $('.menu-toggle'),
    $hasDropdownItem    : $('.site-nav--has-dropdown'),
    $mobileNav          : $('.mobile-nav--sticky'),
    $siteHeader         : $('.site-header'),

    // Product Page
    $productImage       : $('#productPhotoImg'),
    $thumbImages        : $('#productThumbs').find('a.product-photo-thumb'),
    $shareButtons       : $('.social-sharing'),
    $blogSortBy         : $('#blogSortBy'),

    // Cart Page
    cartNoteAdd         : '.cart__note-add',
    cartNote            : '.cart__note',

    // Equal Height Elements
    $gridImages         : $('.grid-image'),

    // Blog and collection filter dropdowns
    $filterDropdowns    : $('.filter-dropdown'),
    $filterSelect       : $('.filter-dropdown__select'),
    $filterLabel        : $('.filter-dropdown__label'),
    $sortDropdown       : $('#sortBy'),
    $tagsDropdown       : $('#sortTags'),

    // Carousels
    $carousel           : $('.carousel-items'),
    $carouselItems      : $('.product-item'),
    carouselItemsSmall  : 1,
    carouselItemsMedium : 2,
    carouselItemsLarge  : 3,

    //RTE
    $rte                : $('.rte')
  };
};

timber.init = function () {
  timber.cacheSelectors();
  timber.toggleMenu();
  timber.productImageSwitch();
  timber.equalHeights();
  timber.responsiveVideos();
  timber.blogFilters();
  timber.setFilterLabels();
  timber.setQueryParams();
  timber.socialSharing();
  timber.cartPage();
  timber.homepageSlider();
  timber.setBreakpoint();
  timber.positionSubNav();
  timber.rteBannerImages();

  // Bind Events
  timber.cache.$sortDropdown.on('change', timber.sortCollection);
  timber.cache.$tagsDropdown.on('change', timber.filterCollection);
  // Wait until fonts load to attempt creating 'more' link in nav
  timber.cache.$window.on('load', timber.responsiveNav);
  timber.cache.$window.on('scroll', timber.detectScroll);
  timber.cache.$window.on('load', timber.equalHeights);
  timber.cache.$window.on('resize', function () {
    afterResize(function() {
      timber.equalHeights();
    }, 250, 'id');
  });

  timber.isInitialized = true;
};

timber.setBreakpoint = function () {
  enquire.register(timber.cache.mediaQuerySmall, {
      match : function() {
        window.bpSmall = true;
        timber.cache.siteHeaderHeight = timber.cache.$siteHeader.outerHeight(true);
        timber.cache.$siteHeader.css('min-height', timber.cache.siteHeaderHeight);

        timber.checkCollectionCarousel(timber.cache.carouselItemsSmall)
      },
      unmatch : function() {
        window.bpSmall = false;
        timber.cache.$siteHeader.removeAttr('style');
        timber.positionSubNav();
      }
  }).register(timber.cache.mediaQueryMedium, {
    match: function () {
      timber.checkCollectionCarousel(timber.cache.carouselItemsMedium);
    }
  }).register(timber.cache.mediaQueryLarge, {
    match: function () {
      timber.checkCollectionCarousel(timber.cache.carouselItemsLarge);
    }
  }, true);
};

timber.detectScroll = function () {
  var currentScrollTop = timber.cache.$window.scrollTop(),
      siteHeaderScrollTop = 0;

  if (!window.bpSmall) {
    return;
  }

  //reset scroll top if ajaxcart is showing
  if (timber.cache.isAjaxCartVisible) {
    siteHeaderScrollTop = timber.cache.$siteHeader.offset().top;
  }

  //if we're still near the top of the page
  if (currentScrollTop <= siteHeaderScrollTop) {
    timber.cache.$mobileNav.removeClass('sticky');
    timber.cache.$mobileNav.removeClass('unsticky');
    timber.cache.scrollPosition = currentScrollTop;
    return;
  }

  //scroll Down && we're below the header
  if (currentScrollTop > timber.cache.scrollPosition && currentScrollTop > (timber.cache.siteHeaderHeight + siteHeaderScrollTop)) {
    timber.cache.$mobileNav.removeClass('sticky');
    timber.cache.$mobileNav.addClass('unsticky');
    timber.cache.scrollPosition = currentScrollTop;
    return;
  }

  //scroll Up
  if (currentScrollTop < timber.cache.scrollPosition) {
    //check if we've scrolled past or are at the bottom of the document as is possible on OSX and iOS
    if (currentScrollTop + window.innerHeight >= document.body.scrollHeight ) {
      return;
    }

    timber.cache.$mobileNav.removeClass('unsticky');
    timber.cache.$mobileNav.addClass('sticky');

    timber.cache.scrollPosition = currentScrollTop;
    return;
  }
};

timber.accessibleNav = function () {
  var $nav          = timber.cache.$navigation,
      $allLinks     = $nav.find('a'),
      $topLevel     = $nav.children('li').find('a'),
      $parents      = $nav.find('.site-nav--has-dropdown'),
      $subMenuLinks = $nav.find('.site-nav--dropdown').find('a'),
      activeClass   = 'nav-hover',
      focusClass    = 'nav-focus';

  // Mouseenter
  $parents.on('mouseenter touchstart', function(evt) {
    var $el = $(this);

    if (!$el.hasClass(activeClass)) {
      evt.preventDefault();
    }

    showDropdown($el);
  });

  // Mouseout
  $parents.on('mouseleave', function() {
    hideDropdown($(this));
  });

  $subMenuLinks.on('touchstart', function(evt) {
    // Prevent touchstart on body from firing instead of link
    evt.stopImmediatePropagation();
  });

  $allLinks.focus(function() {
    handleFocus($(this));
  });

  $allLinks.blur(function() {
    removeFocus($topLevel);
  });

  // accessibleNav private methods
  function handleFocus ($el) {
    var $subMenu   = $el.next('ul'),
        hasSubMenu = $subMenu.hasClass('sub-nav') ? true : false,
        isSubItem  = $('.site-nav--dropdown').has($el).length,
        $newFocus  = null;

    // Add focus class for top level items, or keep menu shown
    if (!isSubItem) {
      removeFocus($topLevel);
      addFocus($el);
    } else {
      $newFocus = $el.closest('.site-nav--has-dropdown').find('a');
      addFocus($newFocus);
    }
  }

  function showDropdown ($el) {
    $el.addClass(activeClass);

    setTimeout(function() {
      timber.cache.$body.on('touchstart', function() {
        hideDropdown($el);
      });
    }, 250);
  }

  function hideDropdown ($el) {
    $el.removeClass(activeClass);
    timber.cache.$body.off('touchstart');
  }

  function addFocus ($el) {
    $el.addClass(focusClass);
  }

  function removeFocus ($el) {
    $el.removeClass(focusClass);
  }
};

timber.responsiveNav = function () {
  timber.cache.$window.on('resize', function () {
    afterResize(function(){
      // Replace original nav items and remove more link
      timber.cache.$navigation.append($('.js-more-menu--list').html());
      $('.js-more-menu').remove();
      timber.alignMenu();
      timber.accessibleNav();
    }, 200, 'uniqueID');
  });
  timber.alignMenu();
  timber.accessibleNav();
  timber.positionSubNav();
};

timber.alignMenu = function () {
  var $nav = timber.cache.$navigation,
      w = 0,
      i = 0,
      wrapperWidth = $nav.outerWidth() - 101,
      menuhtml = '';

  if (window.bpSmall) {
    return;
  }

  $.each($nav.children(), function () {
    var $el = $(this);

    // Ignore hidden customer links (for mobile)
    if (!$el.hasClass('large-hide')) {
      w += $el.outerWidth(true);
    }

    if (wrapperWidth < w) {
      menuhtml += $('<div>').append($el.clone()).html();
      $el.remove();

      // Ignore hidden customer links (for mobile)
      if (!$el.hasClass('large-hide')) {
        i++;
      }
    }
  });

  if (wrapperWidth < w) {
    $nav.append(
      '<li class="js-more-menu site-nav--has-dropdown">'
        + '<a href="#">' + "More" + '<span class="icon icon-arrow-down" aria-hidden="true"></span></a>'
        + '<ul class="js-more-menu--list site-nav--dropdown">' + menuhtml + '</ul></li>'
    );

    timber.cache.$hasDropdownItem = $('.site-nav--has-dropdown');
  }
};

timber.toggleMenu = function () {
  timber.cache.$menuToggle.on('click', function() {
    timber.cache.$body.toggleClass('show-nav');

    // Close ajax cart if open (keep selectors live, modal is inserted with JS)
    if ($('#ajaxifyModal').hasClass('is-visible')) {
      $('#ajaxifyModal').removeClass('is-visible');
      timber.cache.$body.addClass('show-nav');
    }
  });

  // Open sub navs on small screens
  timber.cache.$hasDropdownItem.on('click touchend', function(evt) {
    if (timber.cache.$body.hasClass('show-nav')) {
      var $el = $(this);

      if (!$el.hasClass('show-dropdown')) {
        evt.preventDefault();
        $el.addClass('show-dropdown');
      }
    }
  });
};

timber.productPage = function (options) {
  var moneyFormat = options.moneyFormat,
      variant = options.variant,
      selector = options.selector;

  // Selectors
  var $productImage     = $('#productPhotoImg'),
      $addToCart        = $('#addToCart'),
      $productPrice     = $('#productPrice'),
      $comparePrice     = $('#comparePrice'),
      $quantityElements = $('.quantity-selector, label + .js-qty'),
      $lowStock         = $('.low-stock-container'),
      $variantQuantity  = $('#variantQuantity'),
      $addToCartText    = $('#addToCartText');

  if (variant) {

    // Update variant image, if one is set
    if (variant.featured_image) {
      var newImg = variant.featured_image,
          el = $productImage[0];
      Shopify.Image.switchImage(newImg, el, timber.switchImage);
    }

    // Select a valid variant if available
    if (variant.available) {
      // Available, enable the submit button, change text, show quantity elements
      $addToCart.removeClass('disabled').prop('disabled', false);
      $addToCartText.text('Add to Cart');

      // Show how many items are left, if below 10
      
      if (variant.inventory_management) {
        if (variant.inventory_quantity < 10 && variant.inventory_quantity > 0) {
          $variantQuantity.html("Only 1 left".replace('1', variant.inventory_quantity));
          $lowStock.show();
        } else {
          $lowStock.hide();
        }
      } else {
        //hide low stock warning if inventory isn't being tracked on this product
        $lowStock.hide();
      }
      

    } else {
      // Sold out, disable the submit button, change text, hide quantity elements
      $addToCart.addClass('disabled').prop('disabled', true);
      $addToCartText.text('Sold Out');
      $lowStock.hide();
    }

    // Regardless of stock, update the product price
    $productPrice.html(Shopify.formatMoney(variant.price, moneyFormat));

    // Also update and show the product's compare price if necessary
    if (variant.compare_at_price > variant.price) {
      $comparePrice
        .html(Shopify.formatMoney(variant.compare_at_price, moneyFormat))
        .show();
    } else {
      $comparePrice.hide();
    }
  } else {
    // The variant doesn't exist, disable submit button.
    // This may be an error or notice that a specific variant is not available.
    // To only show available variants, implement linked product options:
    //   - http://docs.shopify.com/manual/configuration/store-customization/advanced-navigation/linked-product-options
    $addToCart.addClass('disabled').prop('disabled', true);
    $addToCartText.text('Unavailable');
    $lowStock.hide();
  }
};

timber.checkCollectionCarousel = function (itemLimit) {
  // not enough items and is a carousel? destroy the carousel!
  if (timber.cache.$carousel && timber.cache.$carousel.find('.owl-item:not(.cloned)').length <= itemLimit) {
    timber.destroyCollectionCarousel();
  }

  // enough items and is not a carousel? create the carousel!
  if (timber.cache.$carousel && timber.cache.$carousel.find('.product-item').length > itemLimit) {
    timber.createCollectionCarousel();
  }
};

timber.createCollectionCarousel = function () {
  var carouselLength = timber.cache.$carouselItems.length,
    carouselOptions = {
      dots: false,
      navSpeed: 100,
      smartSpeed: 100,
      loop: false,
      baseClass: 'owl-carousel',
      navText: [
        '<button type="button" class="carousel__nav-control--prev btn-secondary carousel__nav-control icon-fallback-text"><span class="icon icon-arrow-long-left" aria-hidden="true"></span><span class="fallback-text">prev</span></button>',
        '<button type="button" class="carousel__nav-control--next btn-secondary carousel__nav-control icon-fallback-text"><span class="icon icon-arrow-long-right" aria-hidden="true"></span><span class="fallback-text">next</span></button>'
      ],
      nav: true,
      responsive: {
        0: {
          items: timber.cache.carouselItemsSmall,
          nav: false,
          stagePadding: 65
        },
        321: {
          items: timber.cache.carouselItemsSmall,
          nav: false,
          stagePadding: 80
        },
        400: {
          items: timber.cache.carouselItemsSmall,
          nav: false,
          stagePadding: 90
        },
        480: {
          items: timber.cache.carouselItemsMedium,
          nav: true,
          slideBy: timber.cache.carouselItemsMedium
        },
        769: {
          items: timber.cache.carouselItemsLarge,
          nav: true,
          slideBy: timber.cache.carouselItemsLarge
        }
      }
    };

  // only loop if more than one product so Owl doesn't barf
  if (carouselLength > 1) {
    carouselOptions.loop = true;
  }

  // init owl carousel
  if (carouselLength > 0) {
    timber.cache.$carouselItems.removeClass('fluid-grid-item').addClass('grid-item');
    timber.cache.$carousel.owlCarousel(carouselOptions);
  }
};

timber.destroyCollectionCarousel = function () {
  timber.cache.$carousel.trigger('destroy.owl.carousel');
  timber.cache.$carouselItems.addClass('fluid-grid-item').removeClass('grid-item');
};

timber.carouselEqualHeights = function () {
  var imageHeight = timber.cache.$carousel.find('.owl-item:not(.cloned) .grid-image').height();
  timber.cache.$carousel.find('.cloned .grid-image').css('height', imageHeight);
};

timber.homepageSlider = function (options) {
  var $homepageSlider = $('.homepage-slider__slides'),
      sliderOptions = {
        items: 1,
        loop: true,
        smartSpeed: 750,
        
          animateOut: 'fadeOut',
        
        autoplayTimeout: 5000,
        autoplay: true,
        dots: true,
        center: true,
        nav: false
      };

  if ($homepageSlider.children().length === 1) {
    sliderOptions.loop = false;
    sliderOptions.dots = false;
  }

  if ($homepageSlider.length) {
    $homepageSlider.owlCarousel(sliderOptions);
  }
};

timber.productImageSwitch = function () {
  if (timber.cache.$thumbImages.length) {
    // Switch the main image with one of the thumbnails
    // Note: this does not change the variant selected, just the image
    timber.cache.$thumbImages.on('click', function(evt) {
      evt.preventDefault();
      var newImage = $(this).attr('href');
      timber.switchImage(newImage, null, timber.cache.$productImage);
    });
  }
};

timber.switchImage = function (src, imgObject, el) {
  // Make sure element is a jquery object
  var $el = $(el);
  $el.attr('src', src);
  timber.markActiveThumb($el);
};

timber.markActiveThumb = function ($el) {
  if (timber.isInitialized) {
    timber.cache.$thumbImages.each(function () {
      //cache current thumbnail
      $self = $(this);

      //clean out any previously marked active classes
      $self.removeClass('active');

      //remove querystring from both URLs
      thumbImageUrl = $self.attr('href').split("?")[0];
      linkImageUrl = $el.attr('src').split("?")[0];

      if (thumbImageUrl === linkImageUrl) {
        $self.addClass('active');
        return;
      }
    });
  }
};

timber.blogFilters = function () {
  var path        = window.location.pathname,
      queryParam  = window.location.search,
      tag         = new RegExp('tagged'),
      trimPath    = new RegExp('/tagged.*'),
      isTagUrl    = tag.test(path),
      blogUrl     = path.replace(trimPath, '');

  if (timber.cache.$blogSortBy.length) {
    timber.cache.$blogSortBy.bind('change', function() {

      // if 'all' selected go to blog home
      if ($(this).val() === 'all'){
        window.location = blogUrl;
        return;
      }

      // check to see if blog url has already been filtered by tag or not
      if (isTagUrl) {
        window.location = $(this).val();
        return;
      }

      // if selected is not 'all' and current url is not a tagged url
      window.location = blogUrl + '/tagged/' + jQuery(this).val();
    });
  }
};

timber.setQueryParams = function  () {
  //don't execute if sort dropdown is not present.
  if (!timber.cache.$sortDropdown.length) {
    return;
  }

  Shopify.queryParams = {};
  if (location.search.length) {
    for (var aKeyValue, i = 0, aCouples = location.search.substr(1).split('&'); i < aCouples.length; i++) {
      aKeyValue = aCouples[i].split('=');
      if (aKeyValue.length > 1) {
        Shopify.queryParams[decodeURIComponent(aKeyValue[0])] = decodeURIComponent(aKeyValue[1]);
      }
    }
  }

  timber.cache.$sortDropdown.val(Shopify.queryParams.sort_by);

  if (timber.cache.$html.hasClass('supports-pointerevents') && Shopify.queryParams.sort_by) {
    timber.updateFilterLabel(null, timber.cache.$sortDropdown);
  }
};

timber.filterCollection = function () {
  //check to make sure there is a tag dropdown present
  if (!timber.cache.$tagsDropdown.length) {
    return;
  }

  if (Shopify.queryParams) {
    window.location.href = timber.cache.$tagsDropdown.val() + '?' + $.param(Shopify.queryParams);
  } else {
    window.location.href = timber.cache.$tagsDropdown.val();
  }

};

timber.sortCollection = function () {
  if (!timber.cache.$sortDropdown) {
    return;
  }

  Shopify.queryParams.sort_by = timber.cache.$sortDropdown.val();
  location.search = jQuery.param(Shopify.queryParams);
}

timber.responsiveVideos = function () {
  $('iframe[src*="youtube.com/embed"]').wrap('<div class="video-wrapper"></div>');
  $('iframe[src*="player.vimeo"]').wrap('<div class="video-wrapper"></div>');
  
  // Re-set the src attribute on each iframe after page load
  // for Chrome's "incorrect iFrame content on 'back'" bug.
  // https://code.google.com/p/chromium/issues/detail?id=395791
  $('iframe').each(function() {
    this.src = this.src;
  });
};

timber.setFilterLabels = function () {
  if (timber.cache.$filterSelect.length && timber.cache.$html.hasClass('supports-pointerevents')) {
    timber.cache.$filterSelect.each(function () {
      timber.updateFilterLabel(null, this);
    });

    timber.cache.$filterSelect.on('change', timber.updateFilterLabel);
  }
}

timber.updateFilterLabel = function (evt, element) {
  var $label,
      renderedLabel,
      selectedVariant;

  // set $select based on whether function was called by
  // bound event or not
  var $select = evt ? $(evt.target) : $(element);

  $label = $select.prev('.filter-dropdown__label').find('.filter-dropdown__label--active');
  selectedVariant = $select.find('option:selected').text();
  $label.html(' ' + selectedVariant);
  timber.cache.$filterDropdowns.addClass('loaded');
};

timber.socialSharing = function () {
  

  // General selectors
  var $buttons = timber.cache.$shareButtons,
      $shareLinks = $buttons.find('a'),
      permalink = $buttons.attr('data-permalink');

  // Share button selectors
  var $fbLink = $('.share-facebook'),
      $twitLink = $('.share-twitter'),
      $pinLink = $('.share-pinterest'),
      $googleLink = $('.share-google');

  if ($fbLink.length) {
    $.getJSON('https://graph.facebook.com/?id=' + permalink + '&callback=?', function (data) {
      if (data.shares) {
        $fbLink.find('.share-count').text(data.shares).addClass('is-loaded');
      } else {
        $fbLink.find('.share-count').remove();
      }
    });
  };

  if ($twitLink.length) {
    $.getJSON('https://cdn.api.twitter.com/1/urls/count.json?url=' + permalink + '&callback=?', function (data) {
      if (data.count > 0) {
        $twitLink.find('.share-count').text(data.count).addClass('is-loaded');
      } else {
        $twitLink.find('.share-count').remove();
      }
    });
  };

  if ($pinLink.length) {
    $.getJSON('https://api.pinterest.com/v1/urls/count.json?url=' + permalink + '&callback=?', function (data) {
      if (data.count > 0) {
        $pinLink.find('.share-count').text(data.count).addClass('is-loaded');
      } else {
        $pinLink.find('.share-count').remove();
      }
    });
  };

  if ($googleLink.length) {
    // Can't currently get Google+ count with JS, so just pretend it loaded
    $googleLink.find('.share-count').addClass('is-loaded');
  }

  // Share popups
  $shareLinks.on('click', function(e) {
    e.preventDefault();
    var $el = $(this),
        popup = $el.attr('class').replace('-','_'),
        link = $el.attr('href'),
        w = 700,
        h = 400;

    // Set popup sizes
    switch (popup) {
      case 'share-twitter':
        h = 300;
        break;
      case 'share-fancy':
        w = 480;
        h = 720;
        break;
      case 'share-google':
        w = 500;
        break;
    }

    window.open(link, popup, 'width=' + w + ', height=' + h);
  });
};

timber.positionSubNav = function () {

  timber.cache.$hasDropdownItem.each(function () {
    var $currentPrimaryNav = $(this),
      $currentSubNav = $currentPrimaryNav.find('.site-nav--dropdown')
      primaryNavWidth = Math.floor($currentPrimaryNav.width()),
      subNavWidth = Math.floor($currentSubNav.width()),
      offset = Math.floor((primaryNavWidth - subNavWidth) / 2);

    if (!$currentPrimaryNav.hasClass('js-more-menu')) {
      $currentSubNav.css('left', offset);
    }
  });
};

timber.rteBannerImages = function () {
  if (!timber.cache.$rte) {
    return;
  }

  var $imgContainers = timber.cache.$rte.find('img:not([style])').parent('p');

  if ($imgContainers) {
    $imgContainers.addClass('banner-img');
  }
};

timber.equalHeights = function () {
  // IE8 doesn't handle vertical centering correctly so
  // we'll skip it if on IE8.
  if (timber.cache.$html.hasClass('lt-ie9')) {
    return;
  }

  timber.cache.$gridImages.css('height', 'auto').equalHeights();

  // we need to set the height of cloned elements separately
  if (timber.cache.$carousel) {
    timber.carouselEqualHeights();
  }
};

timber.cartPage = function () {
  

  timber.cache.$body.on('click', timber.cache.cartNoteAdd, function (evt) {
    evt.preventDefault();
    $(this).addClass('is-hidden');
    $(timber.cache.cartNote).addClass('is-active');
    ajaxifyShopify.sizeDrawer();
  });
};

timber.cartToggleCallback = function (data) {
  if (data.is_visible) {
    timber.cache.isAjaxCartVisible = true;
    // timeout used because of Timber bug
    // - https://github.com/Shopify/Timber/issues/244
    setTimeout(function() {
      timber.cartPage();
    }, 1000);
  } else {
    timber.cache.isAjaxCartVisible = false;
  }
};

// Returns a function, that, as long as it continues to be invoked, will not
// be triggered. The function will be called after it stops being called for
// N milliseconds. If `immediate` is passed, trigger the function on the
// leading edge, instead of the trailing.
timber.debounce = function (func, wait, immediate) {
  var timeout;
  return function() {
    var context = this, args = arguments;
    var later = function() {
      timeout = null;
      if (!immediate) func.apply(context, args);
    };
    var callNow = immediate && !timeout;
    clearTimeout(timeout);
    timeout = setTimeout(later, wait);
    if (callNow) func.apply(context, args);
  };
};

// Initialize Timber's JS on docready
$(timber.init);
