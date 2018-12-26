import shareMoodApp from './components/share-mood-app.vue';

// getting language of the PLF 
const lang = eXo ? eXo.env ? eXo.env.portal ? eXo.env.portal.language ? eXo.env.portal.language : 'en' : 'en' : 'en' : 'en';
  
// should expose the locale ressources as REST API
const url = `/share-mood-portlets/mocks/locale_${lang}.json`;

export function init() {
	// getting locale ressources
  exoi18n.loadLanguageAsync(lang, url).then(i18n => {
    // init Vue app when locale ressources are ready
	  new Vue({
	    el: '#moodApplication',
	    render: h => h(shareMoodApp),
	    i18n
	});
  });
}

