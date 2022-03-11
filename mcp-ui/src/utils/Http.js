/**
 * Created by zhuangwei on 2018/6/12.
 */
export default class Http {
  static setPromise(method, url, data) {
    return new Promise((resolve, reject) => {
      switch (method.toUpperCase()) {
        case 'GET':
          axios.get(url, {params: data}).then((res) => {
            if (res) {
              resolve(res.data);
            } else {
              reject();
            }
          })
          break;
        case 'POST':
        case 'PUT':
          axios({method: method, url: url, data: data}).then((res) => {
            if (res) {
              resolve(res.data);
            } else {
              reject();
            }
          })
          break;
        case 'DELETE':
          axios.delete(url, {data: data}).then((res) => {  //后台已RequestBody接收
            if (res) {
              resolve(res.data);
            } else {
              reject();
            }
          });
          break;
      }
    })
  };

  // static get (url, data, loading = true) {
  //   return this.request('GET', url, data, loading)
  // }
  //
  // static post (url, data, loading = true) {
  //   return this.request('POST', url, data, loading)
  // }
}


