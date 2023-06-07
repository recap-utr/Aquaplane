import React from 'react'
import './header.css'

const Header = () => {
  return (
    <header>
        <div className='container header__container'>
            <h1>AQUAPLANE</h1>
            <h5>the <b>A</b>rgument <b>QUA</b>lity ex<b>PLA</b>i<b>NE</b>r</h5>
          
            <p id='description'>Please enter two Arguments to let AQUAPLANE predict which one is more convincing with respect to different Argument Quality dimensions. Alternatively, upload a CSV without Header as Input with semicolon as separator. The arguments must have the same stance, otherwise the results are not reliable.</p>
        </div>
    </header>
  )
}

export default Header