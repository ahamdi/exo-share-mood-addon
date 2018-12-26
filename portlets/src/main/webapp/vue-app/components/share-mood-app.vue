<template>
  <div id="shareMoodApp" class="uiBox"> 
    <div id="mood-selector" v-if="noMoodToday">
      <h6 class="title clearfix">{{ $t("exo.MoodApplication") }}</h6>
      <div class="selectMood">
        <div class="moodItem" @click="updateMood('flaugh')"><span :class="moodClass('flaugh')"></span></div>
        <div class="moodItem" @click="updateMood('smile')"><span :class="moodClass('smile')"></span></div>
        <div class="moodItem" @click="updateMood('speechless')"><span :class="moodClass('speechless')"></span></div>
        <div class="moodItem" @click="updateMood('sad')"><span :class="moodClass('sad')"></span></div>
        <div class="moodItem" @click="updateMood('crying')"><span :class="moodClass('crying')"></span></div>
      </div>
    </div>  
    <div class="moodStats" v-else>
      <div class="span5"><h6 class="title clearfix span2">{{ $t("exo.MyMoodToday") }}</h6></div>
      <div class="selectedMood span2"><span v-bind:class="selectedMoodCSSClass"></span></div>
      <a class="uiIconRefresh uiIconLightGray" @click="noMoodToday = true"></a>
      <select @change="updateStats()" v-model="period" class="">
        <option value="0">{{ $t("exo.lastweek") }}</option>
        <option value="1">{{ $t("exo.lastmonth") }}</option>
        <option value="2">{{ $t("exo.lastthreemonths") }}</option>
        <option value="3">{{ $t("exo.lastsixmonths") }}</option>
      </select>
      <GChart 
        type="ColumnChart"
        :options="chartOptions"
        @ready="onChartReady"
      />
    </div>
  </div>
</template>

<script>
import { GChart } from 'vue-google-charts';
import axios from 'axios';

var showSelectMood = 'true';
export default {
  components: {
  },
  props : {
  },
  data () {
    return {
      noMoodToday : true,
      period : 0,
      myMoodToday : ''
    }
  },
  beforeCreate () {
    axios.get(`/rest/sharemood/loadtoday?username=`+eXo.env.portal.userName)
        .then(response => {
          this.myMoodToday = response.data.mood
          console.log(this.myMoodToday)
          this.noMoodToday = this.myMoodToday == null
          return response.data.mood == null
        })
        .catch(function (error) {
            if (error.response) {
              console.log(error.response)
            } else if (error.request) {
              console.log(error.request)
            } else {
              console.log('unknown error')
            }
            return true;
        })
        },
  methods: {
    updateStats() {
      console.log(this.period)
    },
    onChartReady (chart, google) {
      this.chartsLib = google;
     axios.get(`/rest/sharemood/load?username=`+eXo.env.portal.userName)
      .then(response => {
        var data = new google.visualization.DataTable();
          data.addColumn('string', 'Mood');
          data.addColumn('number', 'Count');
          var jsonData = response.data;
          for (var i = 0; i < jsonData.length; i++) {
            var mood = jsonData[i].mood;
            var count = jsonData[i].count;
            data.addRow([mood, count]);
          }
          const options = {
            title: 'Mood',
            subtitle: 'Your moods',
          };
          chart.draw(data,options)
        })
        .catch(e => {
            //this.errors.push(e)
            console.log(e)
        })
    },
    moodClass : function (mood) {
      return `mood-app-emoticon emoticon-${mood}`;
    },
    updateMood: function (mood) {
      const apiURL = '/rest/sharemood/update';
      const xhr = new XMLHttpRequest();
      var vm = this;
      xhr.open('GET', `${apiURL}?mood=${mood}`);
      xhr.onload = function () {
        const response = xhr.responseText;
        console.log(response)
        vm.noMoodToday = false
        vm.myMoodToday = mood
      };
      xhr.send();
    }
  },
  computed: {
    selectedMoodCSSClass : function() {
      return `mood-app-emoticon emoticon-${this.myMoodToday.toLowerCase()}`;
    }
  }
};
</script>
<style scoped>
.moodItem {
  padding: 0 124px 0 124px;
  height: 35px;
  border: 1px solid #f8f8f8;
  border-radius: 8px;
  background-color: #ededed;
}
.moodItem:hover {
  transform: scaleY(1.2);
}
.mood-app-emoticon {
    display: inline-block;
    /* color: transparent; */
    top: 7px;
    width: 24px;
    height: 24px;
    margin: 5px 5px 0;
    background-repeat: no-repeat;
    background-size: cover;
}
.mood-app-emoticon.emoticon-sad {
  background-image: url('/share-mood-portlets/img/sad.png');
}
.mood-app-emoticon.emoticon-crying {
  background-image: url('/share-mood-portlets/img/crying.png');
}
.mood-app-emoticon.emoticon-speechless {
  background-image: url('/share-mood-portlets/img/speechless.png');
}
.mood-app-emoticon.emoticon-smile {
  background-image: url('/share-mood-portlets/img/smile.png');
}
.mood-app-emoticon.emoticon-flaugh {
  background-image: url('/share-mood-portlets/img/flaugh.png');
}
#shareMoodApp .uiIconRefresh {
  margin-left: 249px;
}
.selectedMood:hover {
  font-family: IONIC-FONT;
  content: "\f21c";
  color: #578dc9;
}
</style>
