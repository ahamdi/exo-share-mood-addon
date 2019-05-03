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
      <v-tabs>
        <v-tab>
          My Mood
        </v-tab>
        <v-tab>
          Everyone
        </v-tab>
      </v-tabs>
      <div class="pull-right">
        <div class="pull-left"><h6>{{ $t("exo.MyMoodToday") }}</h6></div>
        <div class="pull-left" @click="noMoodToday = true">
          <span v-bind:class="selectedMoodCSSClass"></span>
          </div>
      </div>  
      <div class="mood-selectors">
        <select @change="updateStats()" v-model="chartType" class="select-mood">
          <option value="LineChart">{{ $t("exo.trend") }}</option>
          <option value="ColumnChart" selected>{{ $t("exo.stats") }}</option>
        </select>
        <select @change="updateStats()" v-model="period" class="select-mood">
          <option value="lastweek">{{ $t("exo.lastweek") }}</option>
          <option value="lastmonth">{{ $t("exo.lastmonth") }}</option>
          <option value="lastquarter">{{ $t("exo.lastthreemonths") }}</option>
          <option value="lastsimester">{{ $t("exo.lastsixmonths") }}</option>
        </select>
      </div>
      <GChart 
        :type="chartType"
        :options="chartOptions"
        :data="chartData"
        @ready="onChartReady"
      />
      <div class="emoticons-list" v-if="chartType == 'ColumnChart'">
        <span class="mood-app-emoticon emoticon-crying"></span>
        <span class="mood-app-emoticon emoticon-sad"></span>
        <span class="mood-app-emoticon emoticon-speechless"></span>
        <span class="mood-app-emoticon emoticon-smile"></span>
        <span class="mood-app-emoticon emoticon-flaugh"></span>
      </div>
    </div>
    <div  v-if="chartType == 'ColumnChart'">
      <v-progress-linear v-for="item in data" v-bind:key="item.mood"
        :id="item.mood"
        :color="moodColor(item.mood)"
        height="10px"
        :value="item.percent">
      </v-progress-linear>
      </div>
  </div>
</template>

<script>
import { GChart } from 'vue-google-charts';
import axios from 'axios';

var showSelectMood = 'true';
export default {
  components: {
    GChart
  },
  props : {
  },
  data () {
    return {
      noMoodToday : true,
      data: [],
      period : 'lastweek',
      myMoodToday : '',
      chartType : 'stats',
      chartsLib: null,
      chartData: [],
      progressData: [],
      chartType: 'ColumnChart', 
      chartOptions : {
        chart: {
          title: 'Mood',
          subtitle: 'Your moods',
          height: 170
        }
      }
    } 
  },
  beforeCreate () {
    axios.get(`/rest/sharemood/loadtoday?username=`+eXo.env.portal.userName)
        .then(response => {
          this.myMoodToday = response.data.mood;
          console.log(this.myMoodToday) ;
          this.noMoodToday = this.myMoodToday == null;
          return response.data.mood == null;
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
      console.log(this.period);
      var type = this.chartType.localeCompare('LineChart') == 0 ? 'trends' : 'stats';
      axios.get(`/rest/sharemood/load?username=`+eXo.env.portal.userName+'&since='+this.period+'&chartType='+type)
      .then(response => {
        this.data = response.data;
        this.chartData = new google.visualization.DataTable();
        if(type.localeCompare('trends') == 0 ){
          this.chartData.addColumn('date', 'Date');
          this.chartData.addColumn('number', 'Value');
          for (var i = 0; i < this.data.length; i++) {  
              var parsedDate = this.data[i].when.split('-');
              var date = new Date(parsedDate[0],parsedDate[1],parsedDate[2]);
              var value = this.data[i].value;
              this.chartData.addRow([date, value]);
         }
        } else {
          this.chartData.addColumn('string', 'Mood');
          this.chartData.addColumn('number', 'Percentage');
          this.chartData.addColumn({type:'string', role:'tooltip'});
          for (var i = 0; i < this.data.length; i++) {
              var mood = '';
              var percentage = this.data[i].percent;
              this.chartData.addRow([mood, percentage, percentage + '%']);
         }
        }

      })
      .catch(e => {
        //this.errors.push(e)
        console.log(e)
      })
    },
    onChartReady (chart, google) {
     this.chartsLib = google;
      var type = this.chartType.localeCompare('LineChart') == 0 ? 'trends' : 'stats';
      axios.get(`/rest/sharemood/load?username=`+eXo.env.portal.userName+'&since='+this.period+'&chartType='+type)
      .then(response => {
        this.data = response.data;
        this.chartData = new google.visualization.DataTable();
        if(type.localeCompare('trends') == 0 ){
          this.chartData.addColumn('date', 'Date');
          this.chartData.addColumn('number', 'Value');
          for (var i = 0; i < this.data.length; i++) {
              var parsedDate = this.data[i].when.split('-');
              var date = new Date(parsedDate[0],parsedDate[1],parsedDate[2]);
              var value = this.data[i].value;
              this.chartData.addRow([date, value]);
         }
        } else {
          this.chartData.addColumn('string', 'Mood');
          this.chartData.addColumn('number', 'Percentage');
          this.chartData.addColumn({type:'string', role:'tooltip'});
          for (var i = 0; i < this.data.length; i++) {
              var mood = '';
              var percentage = this.data[i].percent;
              this.chartData.addRow([mood, percentage, percentage + '%']);
         }
        }
        chart.draw(this.chartData,this.chartOptions)
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
    },
    moodColor: function(mood) {
      if(mood.localeCompare('CRYING') == 0) return 'red';
      if(mood.localeCompare('SAD') == 0 ) return 'orange';
      if(mood.localeCompare('SPEECHLESS') == 0 ) return 'yellow';
      if(mood.localeCompare('SMILE') == 0 ) return 'green';
      if(mood.localeCompare('FLAUGH') == 0 ) return 'blue';
    }
  },
  computed: {
    selectedMoodCSSClass : function() {
      return `mood-app-emoticon onhover emoticon-${this.myMoodToday.toLowerCase()}`;
    },
    chartOptions () {
      if (!this.chartsLib) return null
      return this.chartsLib.charts.Bar.convertOptions({
        chart: {
          title: 'Mood',
          subtitle: 'Your moods',
          height: 170
        }
      })
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
    margin: 3px;
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
.moodStats .mood-app-emoticon {
  color: transparent;
}
.moodStats .onhover:hover {
  background-image: none;
  color :white;
  background-color: #578dc9;
  width: 20px;
  height: 20px;
}
.moodStats span::after {
  font-family: IONIC-FONT;
  content: "\f21c";
  font-size: 30px;
}
.emoticons-list {
  width: 167px;
  margin: auto;
  padding: 0;
}
.mood-selectors {
  display: inline-flex;
}
.select-mood {
  width: 45%;
  margin: 10px;
}
</style>
