export default {
  user: {
    authenticated: false
  },
  setToken: function(token) {
    localStorage.setItem('token', token)
this.user.authenticated = true
},
getToken: function() {
  return localStorage.getItem('token')
},
removeToken: function() {
  localStorage.removeItem('token')
  this.user.authenticated = false
}
}
