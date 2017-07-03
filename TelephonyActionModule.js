var TelephoneActionModule = {

  callStateUpdated(state) {
    this.callback && this.callback(state)
  }

}

module.exports = TelephoneActionModule;