const path = require('path');
const merge = require('webpack-merge');
const webpackCommonConfig = require('./webpack.common.js');

// the display name of the war
const app = 'share-mood-portlets';

// add the server path to your server location path
const exoServerPath = "/home/exo/work/binaries/5.2/plf-enterprise-mood-5.2.2/platform-5.2.2";

let config = merge(webpackCommonConfig, {
  output: {
    path: path.resolve(`${exoServerPath}/webapps/${app}/`)
  },
  devtool: 'inline-source-map'
});

module.exports = config;
