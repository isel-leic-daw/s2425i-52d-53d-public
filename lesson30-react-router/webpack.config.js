const path = require('path');

module.exports = {
  entry: './src/index.ts',
  mode: 'development',
  devServer: {
    port: 8000,
    historyApiFallback: true,
    compress: false,
    proxy: [
      {
        context: ['/api'],
        target: 'http://localhost:8080',
      },
    ],
  },
  module: {
    rules: [
      {
        test: /\.tsx?$/,
        use: 'ts-loader',
        exclude: /node_modules/,
      },
    ],
  },
  resolve: {
    extensions: ['.tsx', '.ts', '.js'],
  },
  output: {
    filename: 'bundle.js',
    path: path.resolve(__dirname, 'dist'),
  },
};