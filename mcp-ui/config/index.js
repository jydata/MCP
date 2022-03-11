const path = require('path')
module.exports = {
	/*打包前端项目*/
	build: {
		env: require('./prod.env'),
		index: path.resolve(__dirname, '../dist/index.html'),
		assetsRoot: path.resolve(__dirname, '../dist'),
		assetsSubDirectory: 'static',
		assetsPublicPath: '/',
		productionSourceMap: false,
		productionGzip: true,
		productionGzipExtensions: ['js', 'css'],
		bundleAnalyzerReport: process.env.npm_config_report
	},
	/*运行项目*/
	local: {
		env: require('./local.env'),
		port: 8083,
		/*是否打开浏览器*/
		autoOpenBrowser: true,
		assetsSubDirectory: 'static',
		assetsPublicPath: '/',
		proxyTable: {
			'/mcp': {
				target: 'http://192.168.3.131:8886/',
				changeOrigin: true,
				secure: false
			}
		},
		cssSourceMap: false
	},
	dev: {
		env: require('./dev.env'),
		port: 8080,
		/*是否打开浏览器*/
		autoOpenBrowser: true,
		assetsSubDirectory: 'static',
		assetsPublicPath: '/',
		proxyTable: {
			'/mcp': {
				target: 'http://0.0.0.0:8886/',
				changeOrigin: true,
				secure: false
			}
		},
		cssSourceMap: false
	},
	pro: {
		env: require('./prod.env'),
		port: 80,
		/*是否打开浏览器*/
		autoOpenBrowser: true,
		assetsSubDirectory: 'static',
		assetsPublicPath: '/',
		proxyTable: {
			'/mcp': {
				target: '/',
				changeOrigin: true,
				secure: false
			}
		},
		cssSourceMap: false
	}
}
